package com.example.healthydiet.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.service.ReminderReceiver;
import com.example.healthydiet.websocket.WebSocketManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    private TimePicker breakfastTimePicker, lunchTimePicker, dinnerTimePicker;
    private TextInputEditText waterIntervalEditText;
    private Button saveButton;
    private WebSocketManager webSocketManager;
    private static final String CHANNEL_ID = "reminder_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // 初始化 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 初始化视图
        initializeViews();
        createNotificationChannel();

        saveButton.setOnClickListener(v -> saveReminders());

        webSocketManager = WebSocketManager.getInstance();
    }

    private void initializeViews() {
        breakfastTimePicker = findViewById(R.id.breakfastTimePicker);
        lunchTimePicker = findViewById(R.id.lunchTimePicker);
        dinnerTimePicker = findViewById(R.id.dinnerTimePicker);
        waterIntervalEditText = findViewById(R.id.waterIntervalEditText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void saveReminders() {
        // 设置饮食提醒
        setMealReminder("早餐", breakfastTimePicker.getHour(), breakfastTimePicker.getMinute(), 1);
        setMealReminder("午餐", lunchTimePicker.getHour(), lunchTimePicker.getMinute(), 2);
        setMealReminder("晚餐", dinnerTimePicker.getHour(), dinnerTimePicker.getMinute(), 3);

        // 设置饮水提醒
        try {
            int waterInterval = Integer.parseInt(waterIntervalEditText.getText().toString());
            setWaterReminder(waterInterval);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的饮水间隔时间", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "提醒设置成功", Toast.LENGTH_SHORT).show();
    }

    private void setMealReminder(String mealType, int hour, int minute, int requestCode) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("message", "该吃" + mealType + "啦！");
        intent.putExtra("type", "1");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        // 发送到服务器
        String notificationMessage = "createNotification:{" +
                "\"data\": \"该吃" + mealType + "啦！\"," +
                "\"type\": \"1\"," +
                "\"sent\": 1" +
                "}";
        webSocketManager.sendMessage(notificationMessage);
    }

    private void setWaterReminder(int intervalHours) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("message", "该喝水啦！");
        intent.putExtra("type", "2");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
                System.currentTimeMillis(), 
                intervalHours * 60 * 60 * 1000L, 
                pendingIntent);

        // 发送到服务器
        String notificationMessage = "createNotification:{" +
                "\"data\": \"该喝水啦！\"," +
                "\"type\": \"2\"," +
                "\"sent\": 1" +
                "}";
        webSocketManager.sendMessage(notificationMessage);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for Diet Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
} 