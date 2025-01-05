package com.example.healthydiet.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.adapter.PostCommentsAdapter;
import com.example.healthydiet.entity.Comment;
import com.example.healthydiet.entity.Post;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    private Post post;
    private Button commentButton;
    private ListView commentListView;
    private PostCommentsAdapter postCommentsAdapter;
    private WebSocketManager webSocketManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

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

        // 从Intent中获取Post对象
        post = (Post) getIntent().getSerializableExtra("post");

        // 使用获取的Post对象进行操作
        if (post != null) {
            // 显示帖子标题、内容等
            TextView titleTextView = findViewById(R.id.titleTextView);
            titleTextView.setText(post.getPost_title());

            TextView contentTextView = findViewById(R.id.contentTextView);
            contentTextView.setText(post.getPost_content());

            TextView tagsTextView = findViewById(R.id.tagsTextView);
            tagsTextView.setText("标签："+post.getTags());

            TextView timeTextView = findViewById(R.id.timeTextView);
            timeTextView.setText("发布于 "+post.getTimestamp());

        }
        User user=UserManager.getInstance().getUser();
        int is_blocked = user.getIsblocked();
        commentButton = findViewById(R.id.commentButton);

        commentButton.setOnClickListener(v -> {

            if (is_blocked == 0) {
                // 如果未被封禁，执行跳转
                showCommentPopup();
            } else {
                // 如果被封禁，显示提示信息
                Toast.makeText(PostDetailActivity.this, "当前正在封禁中，不能评论", Toast.LENGTH_SHORT).show();
            }
        });

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册 WebSocket 回调
        webSocketManager.registerCallback(WebSocketMessageType.GET_POSTCOMMENTS, message -> {
            Log.d("PostDetailActivity", "Received comments list response: " + message);
            try {
                // 假设后端返回的是一个 JSON 数组
                JSONArray postComments = new JSONArray(message);
                List<Comment> CommentList = new ArrayList<>();

                for (int i = 0; i < postComments.length(); i++) {
                    JSONObject commentJson = postComments.getJSONObject(i);
                    Comment comment = new Comment(
                            commentJson.getString("content"),
                            commentJson.getString("commentUserProfilePicture")
                    );
                    System.out.println(comment.getComment_content());
                    CommentList.add(comment);
                }

                // 在主线程更新 UI
                runOnUiThread(() -> onCommentListUpdated(CommentList));

            } catch (Exception e) {
                Log.e("PostDetailActivity", "Error processing comment list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        String getPostCommentMessage = "getPostComments:" +post.getPost_id();
        Log.d("PostDetailActivity", "Sending post comment message: " + getPostCommentMessage);
        if (!webSocketManager.isConnected()) {
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage(getPostCommentMessage);
    }
    private void showCommentPopup() {
        Dialog dialog = new Dialog(PostDetailActivity.this);
        dialog.setContentView(R.layout.card_comment);
        EditText commentEditText = dialog.findViewById(R.id.commentEditText);

        Button yesButton = dialog.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
            webSocketManager = WebSocketManager.getInstance();
          //  User user = UserManager.getInstance().getUser();
            String comment = commentEditText.getText().toString();


            // 输出结果
            String commentMessage = "createComment:{" +
                    "\"postId\": \"" + post.getPost_id() + "\"," +
                    "\"content\": \"" + comment  +
                    "\"" +"}";
            Log.d("PostDetailActivity", "Sending comment message: " + commentMessage);
            if (!webSocketManager.isConnected()) {
                webSocketManager.reconnect();
            }
            webSocketManager.sendMessage(commentMessage);
            // 刷新评论列表
            webSocketManager.sendMessage("getPostComments:" + post.getPost_id());
            dialog.dismiss();  // 关闭 Dialog
          //  Intent intent = new Intent(PostDetailActivity.this, PostDetailActivity.class);
          //  startActivity(intent);
        });

        Button noButton = dialog.findViewById(R.id.noButton);
        noButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    // 当接收到更新的数据时，这个方法会被调用
    private void onCommentListUpdated(List<Comment> commentList) {
        commentListView = findViewById(R.id.commentListView);
        if (postCommentsAdapter == null) {
            postCommentsAdapter = new PostCommentsAdapter(this, commentList);
            commentListView.setAdapter(postCommentsAdapter);
        } else {
            // 如果适配器已经存在，更新它的数据并刷新
            postCommentsAdapter.updateData(commentList);
            postCommentsAdapter.notifyDataSetChanged();  // 通知适配器数据已更新
        }
    }

}
