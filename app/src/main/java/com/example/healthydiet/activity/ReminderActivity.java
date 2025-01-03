package com.example.healthydiet.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

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
import java.util.Date;
import java.util.TimeZone;

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
    private Switch breakfastSwitch, lunchSwitch, dinnerSwitch, waterSwitch;
    private static final String PREF_BREAKFAST_ENABLED = "breakfast_enabled";
    private static final String PREF_LUNCH_ENABLED = "lunch_enabled";
    private static final String PREF_DINNER_ENABLED = "dinner_enabled";
    private static final String PREF_WATER_ENABLED = "water_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // 请求通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // 请求精确闹钟权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!((AlarmManager) getSystemService(Context.ALARM_SERVICE)).canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

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
        breakfastSwitch = findViewById(R.id.breakfastSwitch);
        lunchSwitch = findViewById(R.id.lunchSwitch);
        dinnerSwitch = findViewById(R.id.dinnerSwitch);
        waterSwitch = findViewById(R.id.waterSwitch);
        
        setupWaterIntervalListeners();

        findViewById(R.id.breakfastButton).setOnClickListener(v -> 
            showTimePicker("早餐", breakfastTimeText));
        findViewById(R.id.lunchButton).setOnClickListener(v -> 
            showTimePicker("午餐", lunchTimeText));
        findViewById(R.id.dinnerButton).setOnClickListener(v -> 
            showTimePicker("晚餐", dinnerTimeText));

        // 加载开关状态
        if (sharedPreferences != null) {
            breakfastSwitch.setChecked(sharedPreferences.getBoolean(PREF_BREAKFAST_ENABLED, false));
            lunchSwitch.setChecked(sharedPreferences.getBoolean(PREF_LUNCH_ENABLED, false));
            dinnerSwitch.setChecked(sharedPreferences.getBoolean(PREF_DINNER_ENABLED, false));
            waterSwitch.setChecked(sharedPreferences.getBoolean(PREF_WATER_ENABLED, false));
        }

        // 添加开关状态改变监听器
        breakfastSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && breakfastTimeText.getText().toString().equals("未设置")) {
                Toast.makeText(this, "请先设置早餐时间", Toast.LENGTH_SHORT).show();
                buttonView.setChecked(false);
            }
        });
        
        lunchSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && lunchTimeText.getText().toString().equals("未设置")) {
                Toast.makeText(this, "请先设置午餐时间", Toast.LENGTH_SHORT).show();
                buttonView.setChecked(false);
            }
        });
        
        dinnerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && dinnerTimeText.getText().toString().equals("未设置")) {
                Toast.makeText(this, "请先设置晚餐时间", Toast.LENGTH_SHORT).show();
                buttonView.setChecked(false);
            }
        });
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
            Log.d("ReminderActivity", "Time picked for " + mealType + ": " + timeString);
        });

        picker.show(getSupportFragmentManager(), "time_picker");
    }

    private void loadSavedSettings() {
        Log.d("ReminderActivity", "Loading saved settings");
        // 加载饮食提醒时间
        String breakfastTime = sharedPreferences.getString(PREF_BREAKFAST, "未设置");
        String lunchTime = sharedPreferences.getString(PREF_LUNCH, "未设置");
        String dinnerTime = sharedPreferences.getString(PREF_DINNER, "未设置");
        int waterHour = sharedPreferences.getInt(PREF_WATER_HOUR, 1);
        int waterMinute = sharedPreferences.getInt(PREF_WATER_MINUTE, 0);

        Log.d("ReminderActivity", "Loaded times - Breakfast: " + breakfastTime 
            + ", Lunch: " + lunchTime 
            + ", Dinner: " + dinnerTime);

        if (!breakfastTime.equals("未设置")) {
            String[] time = breakfastTime.split(":");
            breakfastTimeText.setText(String.format("%02d:%02d", Integer.parseInt(time[0]), Integer.parseInt(time[1])));
        } else {
            breakfastTimeText.setText("未设置");
        }

        if (!lunchTime.equals("未设置")) {
            String[] time = lunchTime.split(":");
            lunchTimeText.setText(String.format("%02d:%02d", Integer.parseInt(time[0]), Integer.parseInt(time[1])));
        } else {
            lunchTimeText.setText("未设置");
        }

        if (!dinnerTime.equals("未设置")) {
            String[] time = dinnerTime.split(":");
            dinnerTimeText.setText(String.format("%02d:%02d", Integer.parseInt(time[0]), Integer.parseInt(time[1])));
        } else {
            dinnerTimeText.setText("未设置");
        }

        waterIntervalHourSeekBar.setProgress(waterHour);
        waterIntervalMinuteSeekBar.setProgress(waterMinute);
        updateWaterIntervalText();
    }

    private void saveReminders() {
        Log.d("ReminderActivity", "Saving reminders");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // 检查是否可以启用提醒
        if (breakfastSwitch.isChecked() && breakfastTimeText.getText().toString().equals("未设置")) {
            Toast.makeText(this, "请先设置早餐时间", Toast.LENGTH_SHORT).show();
            breakfastSwitch.setChecked(false);
        }
        if (lunchSwitch.isChecked() && lunchTimeText.getText().toString().equals("未设置")) {
            Toast.makeText(this, "请先设置午餐时间", Toast.LENGTH_SHORT).show();
            lunchSwitch.setChecked(false);
        }
        if (dinnerSwitch.isChecked() && dinnerTimeText.getText().toString().equals("未设置")) {
            Toast.makeText(this, "请先设置晚餐时间", Toast.LENGTH_SHORT).show();
            dinnerSwitch.setChecked(false);
        }

        // 保存开关状态
        editor.putBoolean(PREF_BREAKFAST_ENABLED, breakfastSwitch.isChecked());
        editor.putBoolean(PREF_LUNCH_ENABLED, lunchSwitch.isChecked());
        editor.putBoolean(PREF_DINNER_ENABLED, dinnerSwitch.isChecked());
        editor.putBoolean(PREF_WATER_ENABLED, waterSwitch.isChecked());

        // 保存时间设置
        String breakfastTime = breakfastTimeText.getText().toString();
        String lunchTime = lunchTimeText.getText().toString();
        String dinnerTime = dinnerTimeText.getText().toString();
        
        editor.putString(PREF_BREAKFAST, breakfastTime);
        editor.putString(PREF_LUNCH, lunchTime);
        editor.putString(PREF_DINNER, dinnerTime);
        editor.putInt(PREF_WATER_HOUR, waterIntervalHourSeekBar.getProgress());
        editor.putInt(PREF_WATER_MINUTE, waterIntervalMinuteSeekBar.getProgress());

        editor.apply();

        Log.d("ReminderActivity", "Saved settings - Breakfast: " + breakfastTime 
            + " (enabled: " + breakfastSwitch.isChecked() + ")"
            + ", Lunch: " + lunchTime 
            + " (enabled: " + lunchSwitch.isChecked() + ")"
            + ", Dinner: " + dinnerTime 
            + " (enabled: " + dinnerSwitch.isChecked() + ")");

        // 设置已启用的提醒
        if (breakfastSwitch.isChecked()) {
            Log.d("ReminderActivity", "Setting breakfast reminder: " + breakfastTime);
            saveMealReminder("早餐", breakfastTimeText.getText().toString(), 1);
        } else {
            Log.d("ReminderActivity", "Cancelling breakfast reminder");
            cancelReminder(1);
        }

        if (lunchSwitch.isChecked()) {
            saveMealReminder("午餐", lunchTimeText.getText().toString(), 2);
        } else {
            cancelReminder(2);
        }

        if (dinnerSwitch.isChecked()) {
            saveMealReminder("晚餐", dinnerTimeText.getText().toString(), 3);
        } else {
            cancelReminder(3);
        }

        if (waterSwitch.isChecked()) {
            int waterHour = waterIntervalHourSeekBar.getProgress();
            int waterMinute = waterIntervalMinuteSeekBar.getProgress();
            setWaterReminder(waterHour, waterMinute);
        } else {
            cancelReminder(4);
        }

        Toast.makeText(this, "提醒设置已保存", Toast.LENGTH_SHORT).show();
    }

    private void saveMealReminder(String mealType, String timeString, int requestCode) {
        Log.d("ReminderActivity", "Saving meal reminder - Type: " + mealType + ", Time: " + timeString);
        if (timeString.equals("未设置")) {
            Log.d("ReminderActivity", "Time not set for " + mealType + ", skipping");
            return;
        }
        
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        setMealReminder(mealType, hour, minute, requestCode);
    }

    private void setMealReminder(String mealType, int hour, int minute, int requestCode) {
        Log.d("ReminderActivity", "Setting meal reminder for " + mealType + " at " + hour + ":" + minute);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            Log.d("ReminderActivity", "Time already passed, setting for tomorrow");
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Log.d("ReminderActivity", "Setting alarm for: " + calendar.getTime() + " (current time: " + new Date() + ")");

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("message", "该吃" + mealType + "啦！");
        intent.putExtra("type", "1");
        intent.putExtra("mealType", mealType);

        // 取消现有的提醒
        PendingIntent existingIntent = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (existingIntent != null) {
            Log.d("ReminderActivity", "Cancelling existing alarm");
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(existingIntent);
            existingIntent.cancel();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("ReminderActivity", "Setting exact alarm with setAlarmClock");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent),
                    pendingIntent
                );
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            }
            
            PendingIntent repeatingIntent = PendingIntent.getBroadcast(this, requestCode + 100,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY,
                AlarmManager.INTERVAL_DAY,
                repeatingIntent
            );
        } else {
            Log.d("ReminderActivity", "Setting repeating alarm");
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        Log.d("ReminderActivity", "Alarm set for " + calendar.getTime().toString());
    }

    private void setWaterReminder(int hours, int minutes) {
        Log.d("ReminderActivity", "Setting water reminder for interval: " + hours + "h " + minutes + "m");
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("message", "该喝水啦！");
        intent.putExtra("type", "2");

        // 取消现有的提醒
        PendingIntent existingIntent = PendingIntent.getBroadcast(this, 4,
                intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (existingIntent != null) {
            Log.d("ReminderActivity", "Cancelling existing water alarm");
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(existingIntent);
            existingIntent.cancel();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long intervalMillis = (hours * 60L + minutes) * 60L * 1000L;
        Log.d("ReminderActivity", "Setting interval: " + intervalMillis + "ms");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("ReminderActivity", "Setting exact alarm for water reminder");
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalMillis,
                intervalMillis,
                pendingIntent
            );
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalMillis,
                pendingIntent
            );
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
                    System.currentTimeMillis(), 
                    intervalMillis,
                    pendingIntent);
        }
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
        
        settings.append("早餐提醒：").append(breakfastSwitch.isChecked() ? "开启" : "关闭");
        if (breakfastSwitch.isChecked()) {
            settings.append(" (").append(breakfastTimeText.getText()).append(")");
        }
        settings.append("\n");

        settings.append("午餐提醒：").append(lunchSwitch.isChecked() ? "开启" : "关闭");
        if (lunchSwitch.isChecked()) {
            settings.append(" (").append(lunchTimeText.getText()).append(")");
        }
        settings.append("\n");

        settings.append("晚餐提醒：").append(dinnerSwitch.isChecked() ? "开启" : "关闭");
        if (dinnerSwitch.isChecked()) {
            settings.append(" (").append(dinnerTimeText.getText()).append(")");
        }
        settings.append("\n");

        settings.append("饮水提醒：").append(waterSwitch.isChecked() ? "开启" : "关闭");
        if (waterSwitch.isChecked()) {
            settings.append(" (每");
            int hours = waterIntervalHourSeekBar.getProgress();
            int minutes = waterIntervalMinuteSeekBar.getProgress();
            if (hours > 0) {
                settings.append(hours).append("小时");
            }
            if (minutes > 0) {
                settings.append(minutes).append("分钟");
            }
            settings.append(")");
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

    private void cancelReminder(int requestCode) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 取消精确闹钟
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        // 取消重复闹钟
        PendingIntent repeatingIntent = PendingIntent.getBroadcast(this, requestCode + 100,
                intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (repeatingIntent != null) {
            alarmManager.cancel(repeatingIntent);
            repeatingIntent.cancel();
        }

        Log.d("ReminderActivity", "Cancelled reminder with request code: " + requestCode);
    }
} 