package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;
import com.google.android.material.textfield.TextInputEditText;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
                // 密码加密
                String encryptedPassword = encryptPassword(password);
                performLogin(phone, encryptedPassword);  // 发送加密后的密码
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

                    int is_admin=response.getInt("isAdmin");
                    if(is_admin==1){
                        Intent intent = new Intent(MainActivity.this, AdminHomepage.class);
                        String profilePicture=response.getString("profilePicture");
                        String name=response.getString("name");
                        intent.putExtra("profilePicture",profilePicture);
                        intent.putExtra("name",name);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        // 提取 userId 字段
                        int userId = response.getInt("id");
                        int isblocked=response.getInt("isBlocked");
                        String profilePicture=response.getString("profilePicture");
                        String name=response.getString("name");
                        int weight=response.getInt("weight");
                        int age=response.getInt("age");
                        int height=response.getInt("height");
                        int gender=response.getInt("gender");
                        double activity_factor=response.getDouble("activityFactor");
                        user = new User(name, password, weight, age, height, phone,gender,activity_factor);
                        user.setProfilePicture(profilePicture);
                        user.setUserId(userId);
                        user.setIsblocked(isblocked);
                        user.setPassword(password);
                        user.setPhone(phone);

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        UserManager.getInstance().setUser(user);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Log.d("MainActivity", "账号或密码错误");
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

    // 使用 SHA-256 对密码进行加密
    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();  // 返回加密后的密码
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void performLogin(String phone, String encryptedPassword) {
        String loginMessage = "login:{" +
                "\"password\": \"" + encryptedPassword + "\"," +  // 使用加密后的密码
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