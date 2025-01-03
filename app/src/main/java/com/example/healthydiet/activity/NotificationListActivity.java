package com.example.healthydiet.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.UserManager;
import com.example.healthydiet.adapter.NotificationAdapter;
import com.example.healthydiet.adapter.UserManageAdapter;
import com.example.healthydiet.entity.Notification;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationListActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;

    private ListView notificationListView;

    private NotificationAdapter notificationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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

        webSocketManager.registerCallback(WebSocketMessageType.GET_USER_NOTIFICATION, message -> {
            Log.d("NotificationList", "Received notify list response: " + message);
            try {
                JSONArray notifylists = new JSONArray(message);
                List<Notification> notificationList = new ArrayList<>();

                for (int i = 0; i < notifylists.length(); i++) {
                    JSONObject notifyJson = notifylists.getJSONObject(i);
                    Notification notification = new Notification(
                          notifyJson.getInt("notificationId"),
                            notifyJson.getString("data"),
                            notifyJson.getInt("userId"),
                            notifyJson.getString("createTime")
                    );

                    notificationList.add(notification);
                }


                // 在主线程更新UI
                runOnUiThread(() -> onNotificationListUpdated(notificationList));
            } catch (Exception e) {
                Log.e("NotificationList", "Error processing notify list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("NotificationList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        User user= UserManager.getInstance().getUser();
        webSocketManager.sendMessage("getUserNotifications:"+user.getUserId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.GET_USER_NOTIFICATION);
    }

    // 当接收到更新的数据时，这个方法会被调用
    private void onNotificationListUpdated(List<Notification> notificationList) {

        notificationListView = findViewById(R.id.notificationListView);
        notificationAdapter = new NotificationAdapter(notificationList,this);
        notificationListView.setAdapter(notificationAdapter);

    }
}
