package com.example.healthydiet.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.example.healthydiet.R;
import com.example.healthydiet.activity.HomeActivity;
import com.example.healthydiet.websocket.WebSocketManager;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "reminder_channel";
    private static final String TAG = "ReminderReceiver";
    private WebSocketManager webSocketManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive triggered");
        String message = intent.getStringExtra("message");
        String type = intent.getStringExtra("type");
        Log.d(TAG, "Received message: " + message + ", type: " + type);

        // 初始化 WebSocketManager
        webSocketManager = WebSocketManager.getInstance();

        // 发送到服务器
        String notificationMessage;
        if (type.equals("1")) {
            String mealType = intent.getStringExtra("mealType");
            notificationMessage = "createNotification:{" +
                    "\"data\": \"该吃" + mealType + "啦！\"," +
                    "\"type\": \"1\"," +
                    "\"sent\": 1" +
                    "}";
        } else {
            notificationMessage = "createNotification:{" +
                    "\"data\": \"该喝水啦！\"," +
                    "\"type\": \"2\"," +
                    "\"sent\": 1" +
                    "}";
        }
        webSocketManager.sendMessage(notificationMessage);

        // 创建点击通知时打开应用的Intent
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Log.d(TAG, "Creating notification");
        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("健康饮食提醒")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // 显示通知
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "Showing notification");
        notificationManager.notify(type.equals("1") ? 1 : 2, builder.build());
    }
} 