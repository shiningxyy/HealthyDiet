package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.User;

import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
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
                registerUser(user);
            }
        });
    }

    private void registerUser(User user) {
        new Thread(() -> {
            try {
                // 设置请求的URL
                URL url = new URL("http://10.0.2.2:8080/api/api/users/register");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000); // 设置超时时间
                connection.setReadTimeout(15000);

                // 设置请求头
                connection.setRequestProperty("Content-Type", "application/json");

                // 启用输出流
                connection.setDoOutput(true);

                // 创建请求体（JSON 格式）
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("name", user.getName());
                jsonBody.put("password", user.getPassword());
                jsonBody.put("weight", user.getWeight());
                jsonBody.put("age", user.getAge());
                jsonBody.put("height", user.getHeight());
                jsonBody.put("phone", user.getPhone());
                jsonBody.put("login", true);

                // 将 JSON 数据写入请求体
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonBody.toString());
                writer.flush();
                writer.close();

                // 获取响应码
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 请求成功
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        // 注册成功后跳转到其他页面
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        startActivity(intent);
                    });
                } else {
                    // 请求失败
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 网络请求失败或异常
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "网络错误，请重试", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
