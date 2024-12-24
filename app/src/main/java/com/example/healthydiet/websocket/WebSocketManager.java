package com.example.healthydiet.websocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketManager {
    private static final String WS_URL = "ws://10.0.2.2:8080/hd/websocket";
    private static WebSocketManager instance;
    private WebSocketClient webSocketClient;
    private Handler handler;
    private boolean isConnecting = false;
    private static final int RECONNECT_DELAY = 3000;
    
    // 用于存储不同类型消息的回调
    private Map<String, WebSocketCallback> callbackMap = new HashMap<>();
    
    // 私有构造函数
    private WebSocketManager() {
        handler = new Handler(Looper.getMainLooper());
        initWebSocket();
    }
    
    // 获取单例实例
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }
    
    private void initWebSocket() {
        if (isConnecting) {
            Log.d("WebSocket", "Connection already in progress");
            return;
        }
        
        if (webSocketClient != null && webSocketClient.isOpen()) {
            Log.d("WebSocket", "WebSocket already connected");
            return;
        }
        
        try {
            isConnecting = true;
            Log.d("WebSocket", "Initializing new WebSocket connection");
            URI uri = URI.create(WS_URL);
            
            // 如果存在旧的连接，先关闭
            if (webSocketClient != null) {
                try {
                    webSocketClient.close();
                } catch (Exception e) {
                    Log.e("WebSocket", "Error closing existing connection: " + e.getMessage());
                }
            }
            
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("WebSocket", "Connected successfully");
                    isConnecting = false;
                }

                @Override
                public void onMessage(String message) {
                    Log.d("WebSocket", "Raw message received: " + message);
                    handleMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("WebSocket", "Connection closed: " + reason + " (code: " + code + ")");
                    isConnecting = false;
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("WebSocket", "WebSocket error: " + ex.getMessage());
                    isConnecting = false;
                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            Log.e("WebSocket", "Error initializing WebSocket: " + e.getMessage());
            isConnecting = false;
            e.printStackTrace();
        }
    }

    // 处理接收到的消息
    private void handleMessage(String message) {
        Log.d("WebSocket", "Received message: " + message);
        try {
            // 首先尝试解析为JSONObject
            if (message.startsWith("{")) {
                // 处理注册响应的特殊情况
                if (message.contains("注册成功")) {
                    // 提取status字段
                    JSONObject partialJson = new JSONObject();
                    partialJson.put("status", 200);
                    String type = determineMessageType(partialJson);
                    WebSocketCallback callback = callbackMap.get(type);
                    if (callback != null) {
                        Log.d("WebSocket", "Found callback for register response");
                        handler.post(() -> callback.onMessage(message));
                    }
                    return;
                }
                
                JSONObject jsonMessage = new JSONObject(message);
                String type = determineMessageType(jsonMessage);
                Log.d("WebSocket", "Determined message type: " + type);
                WebSocketCallback callback = callbackMap.get(type);
                if (callback != null) {
                    Log.d("WebSocket", "Found callback for type: " + type);
                    handler.post(() -> callback.onMessage(message));
                } else {
                    Log.d("WebSocket", "No callback found for type: " + type);
                }
            } 
            // 如果是数组格式，直接当作食物列表处理
            else if (message.startsWith("[") && !message.contains("caloriesPerHour")) {
                Log.d("WebSocket", "Received array message, treating as food list");
                WebSocketCallback callback = callbackMap.get(WebSocketMessageType.FOOD_LIST);
                if (callback != null) {
                    handler.post(() -> callback.onMessage(message));
                }
            }
            else if (message.startsWith("[") && message.contains("caloriesPerHour")) {
                Log.d("WebSocket", "Received array message, treating as exercise list");
                WebSocketCallback callback = callbackMap.get(WebSocketMessageType.EXERCISE_LIST);
                if (callback != null) {
                    handler.post(() -> callback.onMessage(message));
                }
            }
        } catch (Exception e) {
            Log.e("WebSocket", "Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 根据消息内容判断消息类型
    private String determineMessageType(JSONObject jsonMessage) {
        try {
            String rawMessage = jsonMessage.toString();
            Log.d("WebSocket", "Determining message type for: " + rawMessage);
            
            // 登录响应
            if (rawMessage.contains("phone") && !rawMessage.contains("status")) {
                Log.d("WebSocket", "Message type determined as LOGIN");
                return WebSocketMessageType.LOGIN;
            }
            
            // 注册响应
            if (rawMessage.contains("status")) {
                Log.d("WebSocket", "Message type determined as REGISTER");
                return WebSocketMessageType.REGISTER;
            }

            Log.d("WebSocket", "Unknown message type");
            return "";
        } catch (Exception e) {
            Log.e("WebSocket", "Error determining message type: " + e.getMessage());
            return "";
        }
    }

    // 发送消息的方法
    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            Log.d("WebSocket", "Sending message: " + message);
            webSocketClient.send(message);
        } else {
            Log.e("WebSocket", "WebSocket is not connected");
        }
    }

    // 注册回调
    public void registerCallback(String type, WebSocketCallback callback) {
        Log.d("WebSocket", "Registering callback for type: " + type);
        callbackMap.put(type, callback);
    }

    // 移除回调
    public void unregisterCallback(String type) {
        callbackMap.remove(type);
    }

    // 关闭连接
    public void closeConnection() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public boolean isConnected() {
        return webSocketClient != null && webSocketClient.isOpen();
    }

    public void reconnect() {
        Log.d("WebSocket", "Forcing reconnection...");
        if (webSocketClient != null) {
            webSocketClient.close();
        }
        initWebSocket();
    }

    // 添加连接状态的日志方法
    public void logConnectionStatus() {
        if (webSocketClient == null) {
            Log.d("WebSocket", "Connection status: No WebSocket instance");
        } else {
            Log.d("WebSocket", "Connection status: " + 
                (webSocketClient.isOpen() ? "Connected" : "Disconnected") +
                ", isConnecting: " + isConnecting);
        }
    }
} 