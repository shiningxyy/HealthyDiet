package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.User;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText phoneEditText, passwordEditText, usernameEditText, ageEditText, heightEditText, weightEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);  // 设置布局

        // 获取输入框
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.input_password);
        usernameEditText = findViewById(R.id.input_username);
        ageEditText = findViewById(R.id.input_age);
        heightEditText = findViewById(R.id.input_height);
        weightEditText = findViewById(R.id.input_weight);

        // 获取注册按钮并设置点击事件
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            int age = Integer.parseInt(ageEditText.getText().toString().trim());
            int height = Integer.parseInt(heightEditText.getText().toString().trim());
            int weight = Integer.parseInt(weightEditText.getText().toString().trim());

            if (phone.isEmpty() || password.isEmpty() || username.isEmpty() || age <= 0 || height <= 0 || weight <= 0) {
                Toast.makeText(RegisterActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            } else {
                // 创建 User 对象
                User user = new User(username, password, weight, age, height, phone);

                // 调用注册 API
                try {
                    registerUser(user);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void registerUser(User user) throws URISyntaxException {
        // WebSocket 服务器地址
        String wsUrl = "ws://10.0.2.2:8080/hd/websocket";

        // 创建 WebSocket 客户端
        WebSocketClient webSocketClient = new WebSocketClient(new URI(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                // WebSocket 连接建立成功，准备发送登录数据
                try {
                    // 构建包含指令的完整字符串
                    String registerMessage = "register:{" +
                            "\"name\": \"" + user.getName() + "\"," +
                            "\"password\": \"" + user.getPassword() + "\"," +
                            "\"weight\": " + user.getWeight() + "," +
                            "\"age\": " + user.getAge() + "," +
                            "\"height\": " + user.getHeight() + "," +
                            "\"phone\": \"" + user.getPhone() + "\"" +
                            "}";

                    // 打印登录消息，查看是否正确构建
                    Log.d("WebSocket", "loginMessage: " + registerMessage);

                    // 发送登录请求，发送的是整个字符串
                    send(registerMessage);  // 直接发送构建的字符串
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Error sending register data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        if (response.optString("status").equals(200)) {
                            // 注册成功，跳转到主页
                            Toast.makeText(RegisterActivity.this, "register successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();  // 结束当前页面，不能返回
                        } else {
                            // 登录失败，显示错误信息
                            Toast.makeText(RegisterActivity.this, "register failed: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("WebSocket", "Error processing response: " + e.getMessage());
                        Toast.makeText(RegisterActivity.this, "Error processing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                // WebSocket 连接关闭处理
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Connection closed: " + reason, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Exception ex) {
                // WebSocket 发生错误处理
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "WebSocket error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        };

        // 连接 WebSocket
        webSocketClient.connect();
    }
}
