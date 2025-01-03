package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthydiet.R;

public class AdminHomepage extends AppCompatActivity {
    private Button logout;
    private Button user_button;
    private Button post_button;
    private Button comment_button;
    private String profilePicture;
    private String name;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminhomepage);

        profilePicture=getIntent().getStringExtra("profilePicture");
        name=getIntent().getStringExtra("name");

        // 登出
        logout = findViewById(R.id.logoutButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        logout.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(AdminHomepage.this, MainActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        // 用户管理
        user_button = findViewById(R.id.userButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        user_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(AdminHomepage.this, UserManageActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        // 帖子管理
        post_button = findViewById(R.id.postButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        post_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(AdminHomepage.this, PostManageActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        // 评论管理
        comment_button = findViewById(R.id.commentButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        comment_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(AdminHomepage.this, CommentManageActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });
    }
}
