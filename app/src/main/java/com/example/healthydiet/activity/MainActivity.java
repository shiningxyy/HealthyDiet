package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthydiet.R;
import com.example.healthydiet.entity.User;
import com.google.android.material.textfield.TextInputEditText;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText phoneEditText;
    private TextInputEditText passwordEditText;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取输入框
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);

        // 获取登录按钮并设置点击事件
        findViewById(R.id.login_button).setOnClickListener(v -> {
            // 获取账号和密码的值
            phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // 验证输入是否合法
            if (phone.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            } else {
                // 进行登录操作
                try {
                    performLogin(phone, password);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 获取注册按钮并设置点击事件
        findViewById(R.id.register_button).setOnClickListener(v -> {
            // 跳转到注册页面
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(String phone, String password) throws URISyntaxException {
        // WebSocket 服务器地址
        String wsUrl = "ws://10.0.2.2:8080/hd/websocket";

        // 创建 WebSocket 客户端
        WebSocketClient webSocketClient = new WebSocketClient(new URI(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                // WebSocket 连接建立成功，准备发送登录数据
                try {
                    // 构建包含指令的完整字符串
                    String loginMessage = "login:{" +
                            "\"password\": \"" + password + "\"," +
                            "\"phone\": \"" + phone + "\"" +
                            "}";

                    // 打印登录消息，查看是否正确构建
                    Log.d("WebSocket", "loginMessage: " + loginMessage);

                    // 发送登录请求，发送的是整个字符串
                    send(loginMessage);  // 直接发送构建的字符串
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error sending login data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onMessage(String message) {
                Log.d("WebSocket", "Server response: " + message);
                // 处理服务器响应
                runOnUiThread(() -> {
                    try {
                        JSONObject response = new JSONObject(message);
                        // 打印响应对象，检查内容
                        Log.d("WebSocket", "Parsed JSON response: " + response);
                        // 假设服务器返回的是一个状态字段 "status"，并且为 "success" 表示登录成功
                        if (response.optString("phone").equals(phone)) {
                            String username1=response.optString("username");
                            String password1=response.optString("password");
                            String profilePicture=response.optString("profilePicture");
                            int weight1=response.optInt("weight");
                            int age1=response.optInt("age");
                            int height1=response.optInt("height");
                            int isblocked=response.getInt("isblocked");
                            String phone1=response.optString("phone");
                            int userId1=response.optInt("userId");
                            User user = new User(username1, password1, weight1, age1, height1, phone1);
                            user.setUserId(userId1);
                            user.setIsblocked(isblocked);
                            user.setProfilePicture(profilePicture);

                            // 登录成功，跳转到主页
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();  // 结束当前页面，不能返回
                        } else {
                            // 登录失败，显示错误信息
                            Toast.makeText(MainActivity.this, "Login failed: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("WebSocket", "Error processing response: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Error processing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                // WebSocket 连接关闭处理
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Connection closed: " + reason, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Exception ex) {
                // WebSocket 发生错误处理
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "WebSocket error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        };

        // 连接 WebSocket
        webSocketClient.connect();
    }

}