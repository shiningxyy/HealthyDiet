package com.example.healthydiet.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.healthydiet.adapter.FoodListAdapter;
import com.example.healthydiet.adapter.SidebarAdapter;
import com.example.healthydiet.entity.FoodItem;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
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


public class FoodlistActivity extends AppCompatActivity implements SidebarAdapter.OnCategoryClickListener{

    private WebSocketManager webSocketManager;
    private Handler handler;
    private Map<String, List<FoodItem>> foodByType = new HashMap<>();
    private RecyclerView sidebarRecyclerView;
    private RecyclerView foodListRecyclerView;
    private SidebarAdapter sidebarAdapter;
    private FoodListAdapter foodListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);

        // 初始化 Handler，用于在主线程更新 UI
        handler = new Handler(getMainLooper());

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态
        
        // 注册食物列表回调
        webSocketManager.registerCallback(WebSocketMessageType.FOOD_LIST, message -> {
            Log.d("FoodList", "Received food list response: " + message);
            try {
                JSONArray foodItems = new JSONArray(message);
                List<FoodItem> foodItemList = new ArrayList<>();

                for (int i = 0; i < foodItems.length(); i++) {
                    JSONObject foodJson = foodItems.getJSONObject(i);
                    FoodItem foodItem = new FoodItem(
                            foodJson.getString("name"),
                            foodJson.getString("type"),
                            foodJson.getInt("calories"),
                            foodJson.getDouble("carbohydrates"),
                            foodJson.getDouble("dietaryFiber"),
                            foodJson.getDouble("potassium"),
                            foodJson.getDouble("sodium")
                    );
                    int id = foodJson.getInt("foodid");
                    foodItem.setFoodid(id);
                    foodItemList.add(foodItem);
                }

                // 在主线程更新UI
                runOnUiThread(() -> onFoodListUpdated(foodItemList));
            } catch (Exception e) {
                Log.e("FoodList", "Error processing food list: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("FoodList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage("getAllFood");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.unregisterCallback(WebSocketMessageType.FOOD_LIST);
    }

    // 当接收到更新的数据时，这个方法会被调用
    private void onFoodListUpdated(List<FoodItem> foodItemList) {
        // 按类型分类
        foodByType.clear();
        for (FoodItem food : foodItemList) {
            String type = food.getType();
            if (!foodByType.containsKey(type)) {
                foodByType.put(type, new ArrayList<>());
            }
            foodByType.get(type).add(food);
        }

        // 更新 UI
        displayFoodByType();
    }

    // 此方法可以用于展示分类的食物列表
    private void displayFoodByType() {
        // 你可以通过 RecyclerView 或 ListView 按类型展示食物
        // 以下是一个简单的遍历食物类型的示例
        for (String type : foodByType.keySet()) {
            List<FoodItem> items = foodByType.get(type);
            // 在这里，更新你的 RecyclerView 或其他 UI 元素来显示食物
            // 比如设置适配器（Adapter）来显示分类后的食物列表
        }
        sidebarRecyclerView = findViewById(R.id.sidebarRecyclerView);
        foodListRecyclerView = findViewById(R.id.foodListRecyclerView);

        List<String> categories = new ArrayList<>(foodByType.keySet());

        sidebarAdapter = new SidebarAdapter(categories, this);
        sidebarRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sidebarRecyclerView.setAdapter(sidebarAdapter);
        // 获取 RecyclerView 的总高度
        sidebarRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                // 计算每个 item 的高度
                int recyclerViewHeight = sidebarRecyclerView.getHeight();  // 获取 RecyclerView 高度
                int itemCount = sidebarAdapter.getItemCount();  // 获取 item 数量

                // 如果 item 数量大于 0，计算每个 item 的高度
                if (itemCount > 0) {
                    int itemHeight = recyclerViewHeight / itemCount;  // 每个 item 高度 = RecyclerView 高度 / item 数量
                    sidebarAdapter.setItemHeight(itemHeight);  // 设置每个 item 的高度
                }
            }
        });

        foodListAdapter = new FoodListAdapter(new ArrayList<>(), this);
        foodListRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));  // 右侧网格布局，每行2列
        foodListRecyclerView.setAdapter(foodListAdapter);
    }
    @Override
    public void onCategoryClicked(String category) {
        List<FoodItem> foodItems = foodByType.get(category);
        foodListAdapter = new FoodListAdapter(foodItems, this);
        foodListRecyclerView.setAdapter(foodListAdapter);
    }



}
