package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ModifyInfoActivity extends AppCompatActivity {
    private EditText passwordEditText, usernameEditText, ageEditText, heightEditText, weightEditText;
    private Button confirmButton;
    private WebSocketManager webSocketManager;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyinfo);

        // 初始化 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置返回按钮的点击事件
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 显示返回按钮
            getSupportActionBar().setDisplayShowHomeEnabled(true);  // 启用返回按钮图标
        }

        // 返回按钮的点击监听器
        toolbar.setNavigationOnClickListener(v -> {
            // 处理返回操作
            onBackPressed();  // 执行返回操作
        });

        passwordEditText = findViewById(R.id.input_password);
        usernameEditText = findViewById(R.id.input_username);
        ageEditText = findViewById(R.id.input_age);
        heightEditText = findViewById(R.id.input_height);
        weightEditText = findViewById(R.id.input_weight);

        // 获取注册按钮并设置点击事件
        confirmButton = findViewById(R.id.confirm_button);

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册回调
        webSocketManager.registerCallback(WebSocketMessageType.UPDATE_USER, message -> {
            Log.d("ModifyInfoActivity", "Received register response: " + message);
            try {
                if (message.contains("status\":200") || message.contains("用户信息更新成功")) {
                    Log.d("ModifyInfoActivity", "update successful");
                    // 成功处理
                    runOnUiThread(() -> {
                        Toast.makeText(ModifyInfoActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ModifyInfoActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Log.d("ModifyInfoActivity", "update failed");
                    // 注册失败处理
                    runOnUiThread(() ->
                            Toast.makeText(ModifyInfoActivity.this, "update failed", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("ModifyInfoActivity", "Error processing update response: " + e.getMessage());
                e.printStackTrace();
            }
        });

        confirmButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String ageString = ageEditText.getText().toString().trim();
            String heightString = heightEditText.getText().toString().trim();
            String weightString = weightEditText.getText().toString().trim();

            // 检查至少一个字段不为空
            if (password.isEmpty() && username.isEmpty() && ageString.isEmpty() && heightString.isEmpty() && weightString.isEmpty()) {
                Toast.makeText(ModifyInfoActivity.this, "请至少填写一个修改字段", Toast.LENGTH_SHORT).show();
                return;
            }

            // 尝试将数字字段转换为整数
            int age = 0, height = 0;
            double weight = 0;
            try {
                if (!ageString.isEmpty()) {
                    age = Integer.parseInt(ageString);
                }
                if (!heightString.isEmpty()) {
                    height = Integer.parseInt(heightString);
                }
                if (!weightString.isEmpty()) {
                    weight = Integer.parseInt(weightString);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(ModifyInfoActivity.this, "年龄、身高和体重必须是有效的数字", Toast.LENGTH_SHORT).show();
                return;
            }

            // 创建 User 对象并更新信息
            user = UserManager.getInstance().getUser();
            if (user == null) {
                Toast.makeText(ModifyInfoActivity.this, "用户信息加载失败，请重新登录", Toast.LENGTH_SHORT).show();
                return;
            }

            // 更新 User 对象的字段
            if (!password.isEmpty()) {
                user.setPassword(password);
            }
            if (!username.isEmpty()) {
                user.setName(username);
            }
            if (age > 0) {
                user.setAge(age);
            }
            if (height > 0) {
                user.setHeight(height);
            }
            if (weight > 0) {
                user.setWeight(weight);
                if (!webSocketManager.isConnected()) {
                    Log.d("addWeight", "WebSocket not connected, attempting to reconnect...");
                    webSocketManager.reconnect();
                }
                webSocketManager.sendMessage("addWeight:"+weight);
            }

            // 密码加密
            String encryptedPassword = encryptPassword(user.getPassword());
            if (encryptedPassword == null) {
                Toast.makeText(ModifyInfoActivity.this, "密码加密失败", Toast.LENGTH_SHORT).show();
                return;
            }

            // 执行更新
            performUpdate(user, encryptedPassword);
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
    private void performUpdate(User user,String encryptedPassword) {
        String updateMessage = "updateUser:{" +
                "\"name\": \"" + user.getName() + "\"," +
                "\"password\": \"" + encryptedPassword + "\"," +
                "\"weight\": " + user.getWeight() + "," +
                "\"age\": " + user.getAge() + "," +
                "\"height\": " + user.getHeight() +
                "}";
        Log.d("ModifyInfoActivity", "Sending update message: " + updateMessage);

        if (!webSocketManager.isConnected()) {
            Log.d("ModifyInfoActivity", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage(updateMessage);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.UPDATE_USER);
    }
}
