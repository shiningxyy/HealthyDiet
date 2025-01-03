package com.example.healthydiet.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.service.ReminderReceiver;
import com.example.healthydiet.websocket.WebSocketManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    private TextView breakfastTimeText, lunchTimeText, dinnerTimeText;
    private TextView waterIntervalText;
    private Button saveButton;
    private WebSocketManager webSocketManager;
    private static final String CHANNEL_ID = "reminder_channel";
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "ReminderPrefs";
    private static final String PREF_BREAKFAST = "breakfast_time";
    private static final String PREF_LUNCH = "lunch_time";
    private static final String PREF_DINNER = "dinner_time";
    private static final String PREF_WATER_HOUR = "water_interval_hour";
    private static final String PREF_WATER_MINUTE = "water_interval_minute";
    private SeekBar waterIntervalHourSeekBar;
    private SeekBar waterIntervalMinuteSeekBar;

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

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        // 加载保存的设置
        loadSavedSettings();

        Button viewSettingsButton = findViewById(R.id.viewSettingsButton);
        viewSettingsButton.setOnClickListener(v -> showCurrentSettings());
    }

    private void initializeViews() {
        breakfastTimeText = findViewById(R.id.breakfastTimeText);
        lunchTimeText = findViewById(R.id.lunchTimeText);
        dinnerTimeText = findViewById(R.id.dinnerTimeText);
        waterIntervalText = findViewById(R.id.waterIntervalText);
        saveButton = findViewById(R.id.saveButton);
        waterIntervalHourSeekBar = findViewById(R.id.waterIntervalHourSeekBar);
        waterIntervalMinuteSeekBar = findViewById(R.id.waterIntervalMinuteSeekBar);
        
        setupWaterIntervalListeners();

        findViewById(R.id.breakfastButton).setOnClickListener(v -> 
            showTimePicker("早餐", breakfastTimeText));
        findViewById(R.id.lunchButton).setOnClickListener(v -> 
            showTimePicker("午餐", lunchTimeText));
        findViewById(R.id.dinnerButton).setOnClickListener(v -> 
            showTimePicker("晚餐", dinnerTimeText));
    }

    private void setupWaterIntervalListeners() {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateWaterIntervalText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        waterIntervalHourSeekBar.setOnSeekBarChangeListener(listener);
        waterIntervalMinuteSeekBar.setOnSeekBarChangeListener(listener);
    }

    private void updateWaterIntervalText() {
        int hours = waterIntervalHourSeekBar.getProgress();
        int minutes = waterIntervalMinuteSeekBar.getProgress();
        
        StringBuilder text = new StringBuilder();
        if (hours > 0) {
            text.append(hours).append("小时");
        }
        if (minutes > 0 || hours == 0) {
            text.append(minutes).append("分钟");
        }
        
        waterIntervalText.setText(text.toString());
    }

    private void showTimePicker(String mealType, TextView timeText) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("选择" + mealType + "时间")
            .build();

        picker.addOnPositiveButtonClickListener(dialog -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();
            String timeString = String.format("%02d:%02d", hour, minute);
            timeText.setText(timeString);
        });

        picker.show(getSupportFragmentManager(), "time_picker");
    }

    private void loadSavedSettings() {
        // 加载饮食提醒时间
        String breakfastTime = sharedPreferences.getString(PREF_BREAKFAST, "");
        String lunchTime = sharedPreferences.getString(PREF_LUNCH, "");
        String dinnerTime = sharedPreferences.getString(PREF_DINNER, "");
        int waterHour = sharedPreferences.getInt(PREF_WATER_HOUR, 1);
        int waterMinute = sharedPreferences.getInt(PREF_WATER_MINUTE, 0);

        if (!breakfastTime.isEmpty()) {
            String[] time = breakfastTime.split(":");
            breakfastTimeText.setText(String.format("%02d:%02d", Integer.parseInt(time[0]), Integer.parseInt(time[1])));
        }

        if (!lunchTime.isEmpty()) {
            String[] time = lunchTime.split(":");
            lunchTimeText.setText(String.format("%02d:%02d", Integer.parseInt(time[0]), Integer.parseInt(time[1])));
        }

        if (!dinnerTime.isEmpty()) {
            String[] time = dinnerTime.split(":");
            dinnerTimeText.setText(String.format("%02d:%02d", Integer.parseInt(time[0]), Integer.parseInt(time[1])));
        }

        waterIntervalHourSeekBar.setProgress(waterHour);
        waterIntervalMinuteSeekBar.setProgress(waterMinute);
        updateWaterIntervalText();
    }

    private void saveReminders() {
        // 保存饮食提醒
        saveMealReminder("早餐", breakfastTimeText.getText().toString(), 1);
        saveMealReminder("午餐", lunchTimeText.getText().toString(), 2);
        saveMealReminder("晚餐", dinnerTimeText.getText().toString(), 3);

        // 保存饮水提醒
        int waterHour = waterIntervalHourSeekBar.getProgress();
        int waterMinute = waterIntervalMinuteSeekBar.getProgress();
        
        // 确保至少设置了1分钟
        if (waterHour == 0 && waterMinute == 0) {
            waterMinute = 1;
            waterIntervalMinuteSeekBar.setProgress(1);
            updateWaterIntervalText();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_WATER_HOUR, waterHour);
        editor.putInt(PREF_WATER_MINUTE, waterMinute);
        editor.apply();

        setWaterReminder(waterHour, waterMinute);
        Toast.makeText(this, "提醒设置已保存", Toast.LENGTH_SHORT).show();
    }

    private void saveMealReminder(String mealType, String timeString, int requestCode) {
        if (timeString.equals("未设置")) return;
        
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        setMealReminder(mealType, hour, minute, requestCode);
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

    private void setWaterReminder(int hours, int minutes) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("message", "该喝水啦！");
        intent.putExtra("type", "2");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long intervalMillis = (hours * 60L + minutes) * 60L * 1000L;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
                System.currentTimeMillis(), 
                intervalMillis,
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

    // 添加一个方法来获取当前设置的提醒时间
    private String getCurrentSettings() {
        StringBuilder settings = new StringBuilder();
        settings.append("当前提醒设置：\n\n");
        
        String breakfastTime = sharedPreferences.getString(PREF_BREAKFAST, "未设置");
        String lunchTime = sharedPreferences.getString(PREF_LUNCH, "未设置");
        String dinnerTime = sharedPreferences.getString(PREF_DINNER, "未设置");
        int waterHour = waterIntervalHourSeekBar.getProgress();
        int waterMinute = waterIntervalMinuteSeekBar.getProgress();

        settings.append("早餐提醒时间：").append(breakfastTime).append("\n");
        settings.append("午餐提醒时间：").append(lunchTime).append("\n");
        settings.append("晚餐提醒时间：").append(dinnerTime).append("\n");
        settings.append("饮水提醒间隔：");
        if (waterHour > 0 || waterMinute > 0) {
            if (waterHour > 0) {
                settings.append(waterHour).append("小时");
            }
            if (waterMinute > 0) {
                settings.append(waterMinute).append("分钟");
            }
        } else {
            settings.append("未设置");
        }

        return settings.toString();
    }

    // 添加显示当前设置的方法
    private void showCurrentSettings() {
        new AlertDialog.Builder(this)
                .setTitle("提醒设置")
                .setMessage(getCurrentSettings())
                .setPositiveButton("确定", null)
                .show();
    }
} 