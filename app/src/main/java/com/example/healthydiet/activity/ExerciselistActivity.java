package com.example.healthydiet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.example.healthydiet.adapter.ExerciseItemsAdapter;
import com.example.healthydiet.adapter.FoodListAdapter;
import com.example.healthydiet.adapter.SidebarAdapter;
import com.example.healthydiet.entity.ExerciseItem;
import com.example.healthydiet.entity.FoodItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;


public class ExerciselistActivity extends AppCompatActivity{

    private WebSocketManager webSocketManager;
    private Handler handler;
    private ListView exerciseListView;
    private SidebarAdapter sidebarAdapter;
    private ExerciseItemsAdapter exersiceListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exerciselist);

        // 初始化 Handler，用于在主线程更新 UI
        handler = new Handler(getMainLooper());

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册食物列表回调
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
