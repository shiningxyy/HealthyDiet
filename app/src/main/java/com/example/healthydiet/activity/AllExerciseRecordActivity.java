package com.example.healthydiet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.example.healthydiet.UserManager;
import com.example.healthydiet.adapter.ExerciseHistoryAdapter;
import com.example.healthydiet.adapter.ExerciseItemsAdapter;
import com.example.healthydiet.adapter.FoodListAdapter;
import com.example.healthydiet.adapter.SidebarAdapter;
import com.example.healthydiet.entity.ExerciseItem;
import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.entity.FoodItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;


public class AllExerciseRecordActivity extends AppCompatActivity{

    private WebSocketManager webSocketManager;
    private Handler handler;
    private Context context;

    private ListView exerciseListView;
    private SidebarAdapter sidebarAdapter;
    private ExerciseHistoryAdapter exersiceRecordListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewexerciserecord);

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
            Intent intent = new Intent(AllExerciseRecordActivity.this, HomeActivity.class);
            intent.putExtra("fragment_key", "HealthyFragment");

            startActivity(intent);
        });

        // 初始化 Handler，用于在主线程更新 UI
        handler = new Handler(getMainLooper());

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册 WebSocket 回调
        webSocketManager.registerCallback(WebSocketMessageType.EXERCISE_RECORD_GET, message -> {
            Log.d("ExerciseRecord", "Received ExerciseRecord list response: " + message);
            try {
                // 假设后端返回的是一个 JSON 数组
                JSONArray exerciseRecords = new JSONArray(message);
                List<ExerciseRecord> exerciseRecordList = new ArrayList<>();

                for (int i = 0; i < exerciseRecords.length(); i++) {
                    JSONObject exerciseJson = exerciseRecords.getJSONObject(i);
                    ExerciseRecord exerciseRecord = new ExerciseRecord(
                            exerciseJson.getString("exerciseName"),
                            exerciseJson.getString("date"),
                            exerciseJson.getString("duration"),
                            exerciseJson.getInt("burnedCaloris")
                    );
                    exerciseRecordList.add(exerciseRecord);
                }

                // 在主线程更新 UI
                runOnUiThread(() -> onExerciseListUpdated(exerciseRecordList));

            } catch (Exception e) {
                Log.e("ExerciseList", "Error processing exercise list: " + e.getMessage());
                e.printStackTrace();
            }
        });
        User user = UserManager.getInstance().getUser();
        String getUserExerciseRecordMessage = "getUserExerciseRecord:" +user.getUserId();
        Log.d("ExerciseList", "Sending ExerciseList message: " + getUserExerciseRecordMessage);
        if (!webSocketManager.isConnected()) {
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage(getUserExerciseRecordMessage);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.EXERCISE_LIST);
    }

    // 当接收到更新的数据时，这个方法会被调用
    private void onExerciseListUpdated(List<ExerciseRecord> exerciseRecordList) {
        exerciseListView = findViewById(R.id.exerciseItemsListView);
        exersiceRecordListAdapter = new ExerciseHistoryAdapter(this, exerciseRecordList);  // 适配器传递上下文
        exerciseListView.setAdapter(exersiceRecordListAdapter);
    }




}
