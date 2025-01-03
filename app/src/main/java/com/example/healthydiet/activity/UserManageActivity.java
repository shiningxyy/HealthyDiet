package com.example.healthydiet.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.adapter.UserManageAdapter;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserManageActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;

    private ListView userListView;

    private UserManageAdapter userManageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermanage);

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

        webSocketManager.registerCallback(WebSocketMessageType.GET_ALL_USERS, message -> {
            Log.d("UserList", "Received user list response: " + message);
            try {
                JSONArray userlists = new JSONArray(message);
                List<User> userList = new ArrayList<>();

                for (int i = 0; i < userlists.length(); i++) {
                    JSONObject userJson = userlists.getJSONObject(i);
                    User user = new User(
                            userJson.getString("name"),
                            userJson.getString("password"),
                            userJson.getInt("weight"),
                            userJson.getInt("age"),
                            userJson.getInt("height"),
                            userJson.getString("phone"),
                            userJson.getInt("gender"),
                            userJson.getDouble("activityFactor")
                    );
                    user.setUserId(userJson.getInt("id"));
                    user.setIsblocked(userJson.getInt("isBlocked"));
                    userList.add(user);
                }


                // 在主线程更新UI
                runOnUiThread(() -> onUserListUpdated(userList));
            } catch (Exception e) {
                Log.e("UserList", "Error processing user list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("UserList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }

        webSocketManager.sendMessage("getAllUsers:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.GET_ALL_USERS);
    }

    // 当接收到更新的数据时，这个方法会被调用
    private void onUserListUpdated(List<User> userList) {

        userListView = findViewById(R.id.userListView);
        userManageAdapter = new UserManageAdapter(userList,this);
        userListView.setAdapter(userManageAdapter);

    }
}
