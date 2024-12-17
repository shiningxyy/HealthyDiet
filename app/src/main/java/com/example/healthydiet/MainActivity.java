package com.example.healthydiet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取输入框
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        // 获取根布局
        View rootView = findViewById(android.R.id.content); // 根视图通常是 android.R.id.content

        // 设置 EdgeToEdge
        EdgeToEdge.enable(this);

        // 设置 WindowInsets（如果根布局存在）
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取登录按钮并设置点击事件
        findViewById(R.id.login_button).setOnClickListener(v -> {
            // 获取账号和密码的值
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // 验证输入是否合法
            if (username.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            } else {
                // 假设登录成功
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                // 登录成功后跳转到主页面
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);

                // 结束当前页面，使其不能返回
                finish();
            }
        });
    }
}
