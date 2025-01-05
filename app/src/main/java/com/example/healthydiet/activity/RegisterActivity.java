package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
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
    private double activity_factor;
    private int gender;
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
                    int userId = user_response.getInt("id");
                    int isblocked=user_response.getInt("isBlocked");
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

        // 获取 Spinner 组件
        Spinner inputActivityFactor = findViewById(R.id.input_activity_factor);

        // 获取适配器并设置给 Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_factor_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputActivityFactor.setAdapter(adapter);

        // 设置监听器来响应选择的项
        inputActivityFactor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 获取选择的项并调用方法处理
                String selectedOption = parentView.getItemAtPosition(position).toString();
                handleSelection(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 可以处理没有选择的情况
                Toast.makeText(RegisterActivity.this, "必须选择活动频率", Toast.LENGTH_SHORT).show();
            }
        });
        // 设置默认选择项
        inputActivityFactor.setSelection(0); // 默认选择“久坐不动”

        // 获取 Spinner 组件
        Spinner inputGender = findViewById(R.id.input_gender);

        // 获取适配器并设置给 Spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputGender.setAdapter(adapter2);

        // 设置监听器来响应选择的项
        inputGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 获取选择的项并调用方法处理
                String selectedOption = parentView.getItemAtPosition(position).toString();
                handleSelection2(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 可以处理没有选择的情况
                Toast.makeText(RegisterActivity.this, "必须选择性别", Toast.LENGTH_SHORT).show();
            }
        });
        // 设置默认选择项
        inputGender.setSelection(0); // 默认选择“男”

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
                    user = new User(username, password, weight, age, height, phone,gender,activity_factor);
                    // 密码加密
                    String encryptedPassword = encryptPassword(password);
                    performRegister(user,encryptedPassword);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(RegisterActivity.this, "年龄、身高和体重必须是有效的数字", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleSelection(String selectedOption) {
        switch (selectedOption) {
            case "久坐不动":
                // 执行与“久坐不动”相关的操作
                activity_factor=1.2;
                break;

            case "轻度运动":
                activity_factor=1.375;
                break;

            case "中度运动":
                activity_factor=1.55;
                break;

            case "剧烈运动":
                activity_factor=1.725;
                break;

            case "超级剧烈运动":
                activity_factor=1.9;
                break;
            default:
                break;
        }
    }
    private void handleSelection2(String selectedOption) {
        switch (selectedOption) {
            case "男":
                gender=0;
                break;

            case "女":
                gender=1;
                break;

            default:
                break;
        }
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
                "\"gender\": " + user.getGender() + "," +
                "\"activityFactor\": " + user.getActivity_factor() + "," +
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
