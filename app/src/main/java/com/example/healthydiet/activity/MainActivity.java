package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthydiet.R;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText phoneEditText;
    private TextInputEditText passwordEditText;
    String phone;
    private WebSocketManager webSocketManager;

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
            String password = passwordEditText.getText().toString().trim();

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
                    // 登录成功处理
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
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