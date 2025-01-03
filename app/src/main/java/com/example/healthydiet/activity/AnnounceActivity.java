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
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

public class AnnounceActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private Button publishButton;
    private WebSocketManager webSocketManager;
    String tags;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        // 获取输入框
        titleEditText = findViewById(R.id.contentEditText);
        contentEditText = findViewById(R.id.contentEditText);

        publishButton = findViewById(R.id.publishButton);

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册回调
        webSocketManager.registerCallback(WebSocketMessageType.ADD_POST, message -> {
            Log.d("AnnounceActivity", "Received add post response: " + message);
            try {
                if (message.contains("帖子创建成功")) {
                    Log.d("AnnounceActivity", "add post successful");

                    runOnUiThread(() -> {
                        Toast.makeText(AnnounceActivity.this, "帖子发布成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AnnounceActivity.this, AdminHomepage.class);
                        startActivity(intent);
                        finish();
                    });

                }
                else {
                    Log.d("AnnounceActivity", "publish failed");
                    // 发布失败处理
                    runOnUiThread(() ->
                            Toast.makeText(AnnounceActivity.this, "帖子发布失败", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("AnnounceActivity", "Error processing add post response: " + e.getMessage());
                e.printStackTrace();
            }
        });



        publishButton.setOnClickListener(v -> {
            String ptitle = titleEditText.getText().toString().trim();
            String pcontent = contentEditText.getText().toString().trim();


            try {

                if (ptitle.isEmpty() || pcontent.isEmpty()) {
                    Toast.makeText(AnnounceActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                }
                else {
                    String addpost = "createPost:{" +
                            "\"title\": \"" + ptitle + "\"," +
                            "\"content\": \"" + pcontent + "\"," +
                            "\"tags\": \"" + "管理员公告" + "\"" +
                            "}";
                    Log.d("AddPostActivity", "Sending post message: " + addpost);

                    if (!webSocketManager.isConnected()) {
                        Log.d("AddPostActivity", "WebSocket not connected, attempting to reconnect...");
                        webSocketManager.reconnect();
                    }
                    webSocketManager.sendMessage(addpost);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AnnounceActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
