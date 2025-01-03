package com.example.healthydiet.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.adapter.CommentManageAdapter;
import com.example.healthydiet.adapter.PostManageAdapter;
import com.example.healthydiet.entity.Comment;
import com.example.healthydiet.entity.Post;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentManageActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;

    private ListView commentListView;

    private CommentManageAdapter commentManageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentmanage);

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

        webSocketManager.registerCallback(WebSocketMessageType.GET_ALL_COMMENTS, message -> {
            Log.d("CommentManage", "Received comment list response: " + message);
            try {
                JSONArray commentlists = new JSONArray(message);
                List<Comment> commentList = new ArrayList<>();

                for (int i = 0; i < commentlists.length(); i++) {
                    JSONObject commentJson = commentlists.getJSONObject(i);
                    Comment comment = new Comment(
                            commentJson.getInt("postId"),
                            commentJson.getInt("commentId"),
                            commentJson.getString("content"),
                            commentJson.getInt("isOffending")
                    );
                    commentList.add(comment);
                }
                // 在主线程更新UI
                runOnUiThread(() -> onCommentListUpdated(commentList));
            } catch (Exception e) {
                Log.e("CommentManage", "Error processing comment list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("CommentManage", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }

        webSocketManager.sendMessage("getAllComments:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.GET_ALL_COMMENTS);
    }

    // 当接收到更新的数据时，这个方法会被调用
    private void onCommentListUpdated(List<Comment> commentList) {

        commentListView = findViewById(R.id.commentListView);
        commentManageAdapter = new CommentManageAdapter(commentList,this);
        commentListView.setAdapter(commentManageAdapter);

    }
}
