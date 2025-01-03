package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.adapter.ExerciseItemsAdapter;
import com.example.healthydiet.adapter.UserAdapter;
import com.example.healthydiet.entity.ExerciseItem;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;

    private ListView userListView;

    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

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
            Intent intent = new Intent(UserListActivity.this, HomeActivity.class);
            startActivity(intent);
        });


        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        webSocketManager.registerCallback(WebSocketMessageType.EXERCISE_LIST, message -> {
            Log.d("ExerciseList", "Received exercise list response: " + message);
            try {
                JSONArray exerciseItems = new JSONArray(message);
                List<ExerciseItem> exerciseItemList = new ArrayList<>();

                for (int i = 0; i < exerciseItems.length(); i++) {
                    JSONObject exerciseJson = exerciseItems.getJSONObject(i);
                    ExerciseItem exerciseItem = new ExerciseItem(
                            exerciseJson.getString("name"),
                            exerciseJson.getDouble("caloriesPerHour")
                    );
                    int id = exerciseJson.getInt("exerciseId");
                    Log.d("ExerciseList", "id: " + id);

                    exerciseItem.setExerciseId(id);
                    exerciseItemList.add(exerciseItem);
                }



                // 在主线程更新UI
                runOnUiThread(() -> onExerciseListUpdated(exerciseItemList));
            } catch (Exception e) {
                Log.e("ExerciseList", "Error processing exerecise list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("ExerciseList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }

        webSocketManager.sendMessage("getAllExerciseItem");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.EXERCISE_LIST);
    }

    // 当接收到更新的数据时，这个方法会被调用
    private void onExerciseListUpdated(List<ExerciseItem> exerciseItemList) {

        exerciseListView = findViewById(R.id.exerciseItemsListView);

        exersiceListAdapter = new ExerciseItemsAdapter(exerciseItemList,this);
        exerciseListView.setAdapter(exersiceListAdapter);

    }
}
