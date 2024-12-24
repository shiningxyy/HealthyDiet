package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthydiet.R;
import com.example.healthydiet.UserManager;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText phoneEditText;
    private TextInputEditText passwordEditText;
    String phone;
    String password;
    private WebSocketManager webSocketManager;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取输入框
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);

        // 获取登录按钮并设置点击事件
        findViewById(R.id.login_button).setOnClickListener(v -> {
            Log.d("MainActivity", "Login button clicked");
            phone = phoneEditText.getText().toString().trim();
            password= passwordEditText.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("MainActivity", "Attempting login with phone: " + phone);
                performLogin(phone, password);
            }
        });

        // 获取注册按钮并设置点击事件
        findViewById(R.id.register_button).setOnClickListener(v -> {
            // 跳转到注册页面
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态
        
        // 注册登录回调
        webSocketManager.registerCallback(WebSocketMessageType.LOGIN, message -> {
            Log.d("MainActivity", "Received login response: " + message);

            try {
                JSONObject response = new JSONObject(message);
                if (response.optString("phone").equals(phone)) {
                    Log.d("MainActivity", "Login successful");
                    // 提取 userId 字段
                    int userId = response.getInt("userId");
                    int isblocked=response.getInt("isblocked");
                    String profilePicture=response.getString("profilePicture");
                    String name=response.getString("name");
                    int weight=response.getInt("weight");
                    int age=response.getInt("age");
                    int height=response.getInt("height");
                    user = new User(name, password, weight, age, height, phone);
                    user.setProfilePicture(profilePicture);
                    user.setUserId(userId);
                    user.setIsblocked(isblocked);
                    user.setAge(age);
                    user.setName(name);
                    user.setWeight(weight);
                    user.setHeight(height);
                    user.setPassword(password);
                    user.setPhone(phone);
                    // 将 user 对象放入 Intent
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                   // intent.putExtra("user", user); // 将 User 对象传递过去
                    // 使用 UserManager 设置 User 对象
                    UserManager.getInstance().setUser(user);
                    startActivity(intent);

                    finish();
                } else {
                    Log.d("MainActivity", "Login failed: phone numbers don't match");
                    // 登录失败处理
                    runOnUiThread(() -> 
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error processing login response: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void performLogin(String phone, String password) {
        String loginMessage = "login:{" +
                "\"password\": \"" + password + "\"," +
                "\"phone\": \"" + phone + "\"" +
                "}";
        Log.d("MainActivity", "Sending login message: " + loginMessage);
        if (!webSocketManager.isConnected()) {
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage(loginMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.LOGIN);
    }

}