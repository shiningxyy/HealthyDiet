package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthydiet.R;
import com.example.healthydiet.UserManager;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private EditText phoneEditText, passwordEditText, usernameEditText, ageEditText, heightEditText, weightEditText;
    private Button registerButton;
    private WebSocketManager webSocketManager;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 获取输入框
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.input_password);
        usernameEditText = findViewById(R.id.input_username);
        ageEditText = findViewById(R.id.input_age);
        heightEditText = findViewById(R.id.input_height);
        weightEditText = findViewById(R.id.input_weight);

        // 获取注册按钮并设置点击事件
        registerButton = findViewById(R.id.register_button);

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态
        
        // 注册回调
        webSocketManager.registerCallback(WebSocketMessageType.REGISTER, message -> {
            Log.d("RegisterActivity", "Received register response: " + message);
            try {
                if (message.contains("status\":200") || message.contains("注册成功")) {
                    Log.d("RegisterActivity", "Register successful");
                    // 将消息字符串解析为 JSONObject
                    JSONObject response = new JSONObject(message);

                    String userinfo = response.getString("user");
                    JSONObject user_response=new JSONObject(userinfo);
                    // 提取 userId 字段
                    int userId = user_response.getInt("userId");
                    int isblocked=user_response.getInt("isblocked");
                    String profilePicture=user_response.getString("profilePicture");
                    user.setProfilePicture(profilePicture);
                    user.setUserId(userId);
                    user.setIsblocked(isblocked);
                    // 注册成功处理
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        UserManager.getInstance().setUser(user);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Log.d("RegisterActivity", "Register failed");
                    // 注册失败处理
                    runOnUiThread(() -> 
                        Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("RegisterActivity", "Error processing register response: " + e.getMessage());
                e.printStackTrace();
            }
        });

        registerButton.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            
            try {
                int age = Integer.parseInt(ageEditText.getText().toString().trim());
                int height = Integer.parseInt(heightEditText.getText().toString().trim());
                int weight = Integer.parseInt(weightEditText.getText().toString().trim());

                if (phone.isEmpty() || password.isEmpty() || username.isEmpty() || age <= 0 || height <= 0 || weight <= 0) {
                    Toast.makeText(RegisterActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6){
                    Toast.makeText(RegisterActivity.this, "密码长度至少为6位", Toast.LENGTH_SHORT).show();
                }
                else if(!containsLetterAndDigit(password)){
                    Toast.makeText(RegisterActivity.this, "密码必须同时包含数字和字母", Toast.LENGTH_SHORT).show();
                }
                else {
                    // 创建 User 对象
                    user = new User(username, password, weight, age, height, phone);
                    // 密码加密
                    String encryptedPassword = encryptPassword(password);
                    performRegister(user,encryptedPassword);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(RegisterActivity.this, "年龄、身高和体重必须是有效的数字", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public boolean containsLetterAndDigit(String input) {
        // 判断字符串是否同时包含字母和数字
        return input.matches(".*[a-zA-Z].*") && input.matches(".*\\d.*");
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
    private void performRegister(User user,String encryptedPassword) {
        String registerMessage = "register:{" +
                "\"name\": \"" + user.getName() + "\"," +
                "\"password\": \"" + encryptedPassword + "\"," +
                "\"weight\": " + user.getWeight() + "," +
                "\"age\": " + user.getAge() + "," +
                "\"height\": " + user.getHeight() + "," +
                "\"phone\": \"" + user.getPhone() + "\"" +
                "}";
        Log.d("RegisterActivity", "Sending register message: " + registerMessage);
        
        if (!webSocketManager.isConnected()) {
            Log.d("RegisterActivity", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage(registerMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.REGISTER);
    }
}
