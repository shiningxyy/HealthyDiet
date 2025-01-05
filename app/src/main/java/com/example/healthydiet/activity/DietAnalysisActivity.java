package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.entity.FoodItem;
import com.example.healthydiet.entity.FoodRecord;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class DietAnalysisActivity extends AppCompatActivity {
    private List<FoodRecord> TodayRecordList;
    private int total_calories;
    private double total_fat=0;
    private double total_protein=0;
    private double total_carbohydrates=0;
    private double total_sodium=0;
    private double total_potassium=0;
    private double total_dietaryFiber=0;
    private double generation;
    private ProgressBar progressBar;
    private TextView caloriesTextView;  // 用来显示摄入千卡数
    private TextView hintTextView;
    private TableLayout tableLayout;

    private TableLayout tableLayoutfood;

    private Button recipeRecommendationButton;

    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietanalysis);

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

        // 获取传递过来的 List
        Intent intent = getIntent();
        TodayRecordList = (List<FoodRecord>) intent.getSerializableExtra("TodayRecordList");
        total_calories=getIntent().getIntExtra("total_calories", 0);
        total_fat=getIntent().getDoubleExtra("total_fat", 0);
        total_protein=getIntent().getDoubleExtra("total_protein", 0);
        total_carbohydrates=getIntent().getDoubleExtra("total_carbohydrates", 0);
        total_sodium=getIntent().getDoubleExtra("total_sodium", 0);
        total_potassium=getIntent().getDoubleExtra("total_potassium", 0);
        total_dietaryFiber=getIntent().getDoubleExtra("total_dietaryFiber", 0);
        generation=getIntent().getDoubleExtra("generation", 0);


        // 初始化进度条
        progressBar = findViewById(R.id.progress_bar);
        // 设置进度条的最大值和当前进度
        progressBar.setMax((int)generation);  // 设置最大进度为 2000
        progressBar.setProgress(total_calories);  // 将 total_calories 作为当前进度

        caloriesTextView = findViewById(R.id.TextView2);
        String caloriesText = "今日已摄入 " + total_calories + " 千卡";
        caloriesTextView.setText(caloriesText);

        hintTextView = findViewById(R.id.TextView1);
        String hintText = "根据毛德倩公式，每日应摄入热量 " + (int)generation + " 千卡";
        hintTextView.setText(hintText);

        tableLayout = findViewById(R.id.table_layout);  // 获取 TableLayout
        addTableRow("项目","摄入量");
        addTableRow("脂肪",total_fat+"克");
        addTableRow("蛋白质",total_protein+"克");
        addTableRow("碳水化合物",total_carbohydrates+"克");
        addTableRow("膳食纤维",total_dietaryFiber+"克");
        addTableRow("钠",total_sodium+"毫克");
        addTableRow("钾",total_potassium+"毫克");

        User user = UserManager.getInstance().getUser();
        tableLayoutfood = findViewById(R.id.table_layout_food);  // 获取 TableLayout

        double[] userProfile = {2000-total_calories, 2000*0.45f/4.0-total_carbohydrates, 25.0-total_dietaryFiber, 4700.0-total_potassium,1500.0-total_sodium, 2000*0.2f/9.0-total_fat, user.getWeight()*0.8-total_protein};

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
                            foodJson.getDouble("sodium"),
                            foodJson.getDouble("fat"),
                            foodJson.getDouble("protein")
                    );
                    int id = foodJson.getInt("foodid");
                    foodItem.setFoodid(id);
                    foodItemList.add(foodItem);
                }
                List<FoodItem> recommendedFoods = recommendTopNFoods(foodItemList, userProfile, 6);

                displayRecommendedFoods(recommendedFoods);
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
    private void addTableRow(String item, String calories) {
        // 创建新的 TableRow
        TableRow tableRow = new TableRow(this);

        // 创建 TextView 显示项目名称
        TextView itemTextView = new TextView(this);
        itemTextView.setText(item);
        itemTextView.setPadding(20, 0, 50, 0);  // 设置左边和右边的内边距（增加列间距）
        tableRow.addView(itemTextView);  // 将项目添加到表格行中
        itemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);  // 设置字体大小为 20sp

        // 创建 TextView 显示摄入量
        TextView caloriesTextView = new TextView(this);
        caloriesTextView.setText(calories);
        caloriesTextView.setPadding(50, 0, 20, 0);  // 设置左边和右边的内边距（增加列间距）
        tableRow.addView(caloriesTextView);  // 将摄入量添加到表格行中
        caloriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);  // 设置字体大小为 20sp


        // 将新创建的 TableRow 添加到 TableLayout 中
        tableLayout.addView(tableRow);
    }
    private void displayRecommendedFoods(List<FoodItem> recommendedFoods) {
        // 清空之前的推荐数据（如果有的话）
        tableLayoutfood.removeAllViews();

        // 每行最多放两个食物
        int columnCount = 2;
        TableRow tableRow = null;

        for (int i = 0; i < recommendedFoods.size(); i++) {
            // 如果 tableRow 为空或者已经添加了两个元素，创建一个新的 TableRow
            if (i % columnCount == 0) {
                tableRow = new TableRow(this);
            }

            // 获取当前食物
            FoodItem food = recommendedFoods.get(i);

            // 创建一个 LinearLayout 来垂直排列图片和文本
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);  // 设置方向为垂直排列
            linearLayout.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL); // 使整个 LinearLayout 在水平方向上居中

            // 创建 TextView 显示食物名称
            TextView nameTextView = new TextView(this);
            nameTextView.setText(food.getName());
            nameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            nameTextView.setTextSize(18);
            nameTextView.setPadding(16, 0, 16, 0);
            linearLayout.addView(nameTextView);

            // 创建 TextView 显示食物的热量
            TextView caloriesTextView = new TextView(this);
            caloriesTextView.setText(food.getCalories() + " kcal/100g");
            caloriesTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            caloriesTextView.setTextSize(18);
            caloriesTextView.setPadding(16, 0, 16, 0);
            linearLayout.addView(caloriesTextView);

            // 将 LinearLayout 添加到 TableRow 中
            tableRow.addView(linearLayout);

            // 如果当前行已满（即有两个元素），将 TableRow 添加到 TableLayout 中
            if ((i + 1) % columnCount == 0 || i == recommendedFoods.size() - 1) {
                tableLayoutfood.addView(tableRow);
            }
        }
    }




    private double calculateSimilarity(double[] userPreferences, FoodItem food) {
        double dotProduct = 0;
        double userMagnitude = 0;
        double foodMagnitude = 0;
        double[] foodAttributes = {food.getCalories(), food.getCarbohydrates(), food.getDietaryFiber(), food.getPotassium(),food.getSodium(), food.getFat(), food.getProtein()};

        for (int i = 0; i < userPreferences.length; i++) {
            dotProduct += userPreferences[i] * foodAttributes[i];
            userMagnitude += Math.pow(userPreferences[i], 2);
            foodMagnitude += Math.pow(foodAttributes[i], 2);
        }

        return dotProduct / (Math.sqrt(userMagnitude) * Math.sqrt(foodMagnitude));
    }
    public List<FoodItem> recommendTopNFoods(List<FoodItem> foods, double[] userPreferences, int N) {
        // 创建一个优先队列，按相似度降序排列
        PriorityQueue<FoodItem> queue = new PriorityQueue<>(new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem f1, FoodItem f2) {
                // 计算食物与用户偏好之间的相似度
                double similarity1 = calculateSimilarity(userPreferences, f1);
                double similarity2 = calculateSimilarity(userPreferences, f2);

                // 按相似度降序排列
                return Double.compare(similarity2, similarity1);
            }
        });

        // 将所有食物添加到优先队列中
        for (FoodItem food : foods) {
            queue.add(food);
        }

        // 取出前N个食物
        List<FoodItem> recommendedFoods = new ArrayList<>();
        for (int i = 0; i < N && !queue.isEmpty(); i++) {
            recommendedFoods.add(queue.poll());
        }

        return recommendedFoods;
    }

}
