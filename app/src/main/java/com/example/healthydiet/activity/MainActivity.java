package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthydiet.R;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText phoneEditText;
    private TextInputEditText passwordEditText;

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
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // 验证输入是否合法
            if (phone.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            } else {
                // 进行登录操作
                performLogin(phone, password);
            }
        });

        // 获取注册按钮并设置点击事件
        findViewById(R.id.register_button).setOnClickListener(v -> {
            // 跳转到注册页面
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(String phone, String password) {
        // 创建请求的 URL
        String urlString = "http://10.0.2.2:8080/api/api/users/login";

        // 创建请求数据的 JSON 格式字符串
        String jsonBody = "{"
                + "\"phone\": \"" + phone + "\","
                + "\"password\": \"" + password + "\""
                + "}";

        // 使用 HttpURLConnection 发送 POST 请求
        new Thread(() -> {
            try {
                // 创建 URL 对象
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // 设置请求方法
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);  // 设置连接超时
                connection.setReadTimeout(5000);     // 设置读取超时

                // 设置请求头
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);  // 允许向输出流写数据

                // 写入请求体
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // 获取服务器响应代码
                int responseCode = connection.getResponseCode();

                // 读取响应
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        responseCode == HttpURLConnection.HTTP_OK
                                ? connection.getInputStream()
                                : connection.getErrorStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 判断请求是否成功
                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 登录成功
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();  // 结束当前页面，不能返回
                    } else {
                        // 登录失败
                        Toast.makeText(MainActivity.this, "Login failed: " + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

}