package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.adapter.FoodRecordAdapter;

import com.example.healthydiet.entity.FoodRecord;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewFoodRecordActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;
    private List<FoodRecord>FoodRecordList;
    private RecyclerView recyclerView;
    private FoodRecordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewfoodrecord);

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
            Intent intent = new Intent(ViewFoodRecordActivity.this, HomeActivity.class);
            intent.putExtra("fragment_key", "DietFragment");

            startActivity(intent);
        });
        recyclerView = findViewById(R.id.food_record_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 使用垂直线性布局

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册食物记录列表回调
        webSocketManager.registerCallback(WebSocketMessageType.FOOD_RECORD_GET, message -> {
            Log.d("FoodRecordList", "Received food record list response: " + message);
            try {
                JSONArray foodLists = new JSONArray(message);
                FoodRecordList = new ArrayList<>();

                for (int i = 0; i < foodLists.length(); i++) {
                    JSONObject foodJson = foodLists.getJSONObject(i);
                    FoodRecord foodItem = new FoodRecord(
                            foodJson.getInt("foodRecordId"),
                            foodJson.getString("foodname"),
                            foodJson.getString("recordTime"),
                            foodJson.getInt("userId"),
                            foodJson.getInt("foodId"),
                            foodJson.getDouble("foodWeight"),
                            foodJson.getInt("calories"),
                            foodJson.getDouble("fat"),
                            foodJson.getDouble("protein"),
                            foodJson.getDouble("carbohydrates"),
                            foodJson.getDouble("sodium"),
                            foodJson.getDouble("potassium"),
                            foodJson.getDouble("dietaryFiber")
                    );

                    FoodRecordList.add(foodItem);
                }

                // 在主线程更新UI
                runOnUiThread(() -> {
                    adapter = new FoodRecordAdapter(FoodRecordList);
                    recyclerView.setAdapter(adapter);
                });
            } catch (Exception e) {
                Log.e("FoodRecordList", "Error processing food record list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("FoodList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        User user = UserManager.getInstance().getUser();
        String getRecord="getAllFoodRecord:" +user.getUserId();
        webSocketManager.sendMessage(getRecord);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.FOOD_RECORD_GET);
    }
}
