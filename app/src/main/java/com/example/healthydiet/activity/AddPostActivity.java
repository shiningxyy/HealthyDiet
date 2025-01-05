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

public class AddPostActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private Button publishButton;
    private WebSocketManager webSocketManager;
    String tags;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        // 获取输入框
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        publishButton = findViewById(R.id.publishButton);

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册回调
        webSocketManager.registerCallback(WebSocketMessageType.ADD_POST, message -> {
            Log.d("AddPostActivity", "Received add post response: " + message);
            try {
                if (message.contains("帖子创建成功")) {
                    Log.d("AddPostActivity", "add post successful");

                    runOnUiThread(() -> {
                        Toast.makeText(AddPostActivity.this, "帖子发布成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPostActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    });

                }
                else {
                    Log.d("AddPostActivity", "publish failed");
                    // 发布失败处理
                    runOnUiThread(() ->
                            Toast.makeText(AddPostActivity.this, "帖子发布失败", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("AddPostActivity", "Error processing add post response: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 获取 Spinner 组件
        Spinner inputActivityFactor = findViewById(R.id.tagsSpinner);

        // 获取适配器并设置给 Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tags_array, android.R.layout.simple_spinner_item);
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
                Toast.makeText(AddPostActivity.this, "必须选择标签", Toast.LENGTH_SHORT).show();
            }
        });
        // 设置默认选择项
        inputActivityFactor.setSelection(0); // 默认选择“饮食”

        publishButton.setOnClickListener(v -> {
            String ptitle = titleEditText.getText().toString().trim();
            String pcontent = contentEditText.getText().toString().trim();


            try {

                if (ptitle.isEmpty() || pcontent.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                }
                else {
                    String addpost = "createPost:{" +
                            "\"title\": \"" + ptitle + "\"," +
                            "\"content\": \"" + pcontent + "\"," +
                            "\"tags\": \"" + tags + "\"" +
                            "}";
                    Log.d("AddPostActivity", "Sending post message: " + addpost);

                    if (!webSocketManager.isConnected()) {
                        Log.d("AddPostActivity", "WebSocket not connected, attempting to reconnect...");
                        webSocketManager.reconnect();
                    }
                    webSocketManager.sendMessage(addpost);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddPostActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSelection(String selectedOption) {
        switch (selectedOption) {
            case "饮食":
                tags="饮食";
                break;

            case "运动":
                tags="运动";
                break;

            case "健康":
                tags="健康";
                break;

            default:
                break;
        }
    }
}
