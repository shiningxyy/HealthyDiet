package com.example.healthydiet.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.adapter.PostManageAdapter;
import com.example.healthydiet.adapter.UserManageAdapter;
import com.example.healthydiet.entity.Post;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostManageActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;

    private ListView postListView;

    private PostManageAdapter postManageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postmanage);

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

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        webSocketManager.registerCallback(WebSocketMessageType.GET_ALL_POSTS, message -> {
            Log.d("PostManage", "Received post list response: " + message);
            try {
                JSONArray postlists = new JSONArray(message);
                List<Post> postList = new ArrayList<>();

                for (int i = 0; i < postlists.length(); i++) {
                    JSONObject postJson = postlists.getJSONObject(i);
                    Post post = new Post(
                            postJson.getInt("postId"),
                            postJson.getString("title"),
                            postJson.getInt("isOffending")
                    );
                    postList.add(post);
                }


                // 在主线程更新UI
                runOnUiThread(() -> onPostListUpdated(postList));
            } catch (Exception e) {
                Log.e("PostManage", "Error processing post list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("PostManage", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }

        webSocketManager.sendMessage("getAllPosts:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.GET_ALL_POSTS);
    }

    // 当接收到更新的数据时，这个方法会被调用
    private void onPostListUpdated(List<Post> postList) {

        postListView = findViewById(R.id.postListView);
        postManageAdapter = new PostManageAdapter(postList,this);
        postListView.setAdapter(postManageAdapter);

    }
}
