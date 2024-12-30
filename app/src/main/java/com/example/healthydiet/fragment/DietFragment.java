package com.example.healthydiet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.healthydiet.R;
import com.example.healthydiet.UserManager;
import com.example.healthydiet.activity.DietAnalysisActivity;
import com.example.healthydiet.activity.FoodlistActivity;
import com.example.healthydiet.activity.MainActivity;
import com.example.healthydiet.activity.RegisterActivity;
import com.example.healthydiet.activity.ViewFoodRecordActivity;
import com.example.healthydiet.adapter.FoodRecordAdapter;
import com.example.healthydiet.entity.FoodRecord;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// DietFragment.java
public class DietFragment extends Fragment {
    private ProgressBar circularProgressBar;
    private TextView caloriesTextView;  // 用来显示摄入千卡数
    private Button breakfast_button;
    private Button lunch_button;
    private Button dinner_button;
    private Button view_record;
    private Button diet_analysis;
    private WebSocketManager webSocketManager;
    private List<FoodRecord> TodayRecordList;
    private int total_calories=0;
    private double total_fat=0;
    private double total_protein=0;
    private double total_carbohydrates=0;
    private double total_sodium=0;
    private double total_potassium=0;
    private double total_dietaryFiber=0;
    //private User user;
    public DietFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);

        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        caloriesTextView = view.findViewById(R.id.caloriesTextView);

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.logConnectionStatus();  // 记录连接状态

        // 注册食物记录列表回调
        webSocketManager.registerCallback(WebSocketMessageType.FOOD_RECORD_GET, message -> {
            Log.d("FoodRecordList", "Received food record list response: " + message);
            try {
                JSONArray foodLists = new JSONArray(message);
                TodayRecordList = new ArrayList<>();

                for (int i = 0; i < foodLists.length(); i++) {
                    JSONObject foodJson = foodLists.getJSONObject(i);
                    String record_time=foodJson.getString("recordTime");
                    if(istoday(record_time)){
                        int calories= foodJson.getInt("calories");
                        int food_record_id=foodJson.getInt("foodRecordId");
                        String food_name=foodJson.getString("foodname");
                        int user_id=foodJson.getInt("userId");
                        int food_id=foodJson.getInt("foodId");
                        double food_weight=foodJson.getDouble("foodWeight");
                        double fat=foodJson.getDouble("fat");
                        double protein=foodJson.getDouble("protein");
                        double carbohydrates=foodJson.getDouble("carbohydrates");
                        double sodium=foodJson.getDouble("sodium");
                        double potassium=foodJson.getDouble("potassium");
                        double dietaryFiber=foodJson.getDouble("dietaryFiber");
                        FoodRecord today_record=new FoodRecord(food_record_id,food_name,record_time,
                                user_id,food_id, food_weight,calories,fat,protein,carbohydrates,sodium,
                                potassium,dietaryFiber);
                        TodayRecordList.add(today_record);
                        total_calories+=calories;
                        total_potassium+=potassium;
                        total_protein+=protein;
                        total_carbohydrates+=carbohydrates;
                        total_dietaryFiber+=dietaryFiber;
                        total_fat+=fat;
                        total_sodium+=sodium;
                        System.out.println("fat："+fat);
                        System.out.println("total_fat："+total_fat);
                    }
                }
               // System.out.println("列表长度："+foodLists.length());
               //  System.out.println("总卡路里："+total_calories);

                    // 更新进度条
                updateProgressBar(total_calories);
                updateCaloriesText(total_calories);

            } catch (Exception e) {
                Log.e("FoodRecordList", "Error processing food record list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // 确保WebSocket已连接后再发送请求
        if (!webSocketManager.isConnected()) {
            Log.d("FoodRecordList", "WebSocket not connected, attempting to reconnect...");
            webSocketManager.reconnect();
        }
        User user = UserManager.getInstance().getUser();
        String getRecord="getAllFoodRecord:" +user.getUserId();
        webSocketManager.sendMessage(getRecord);


        // 初始化跳转按钮
        breakfast_button = view.findViewById(R.id.breakfastButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        breakfast_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), FoodlistActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            // 将 user 对象传递给目标 Activity
          //  intent.putExtra("user", user); // 将 user 对象作为 Extra 传递
            startActivity(intent);
        });

        // 初始化跳转按钮
        lunch_button = view.findViewById(R.id.lunchButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        lunch_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), FoodlistActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        // 初始化跳转按钮
        dinner_button = view.findViewById(R.id.dinnerButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        dinner_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), FoodlistActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        // 初始化跳转按钮
        view_record = view.findViewById(R.id.viewRecordButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        view_record.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), ViewFoodRecordActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            startActivity(intent);
        });

        diet_analysis = view.findViewById(R.id.details_button);
        // 设置按钮点击事件，跳转到另一个 Activity
        diet_analysis.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), DietAnalysisActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            // 将 List 传递到目标 Activity
            intent.putExtra("TodayRecordList", (Serializable) TodayRecordList);  // 传递序列化的 List
            intent.putExtra("total_calories",total_calories);
            intent.putExtra("total_fat",total_fat);
            intent.putExtra("total_sodium",total_sodium);
            intent.putExtra("total_dietaryFiber",total_dietaryFiber);
            intent.putExtra("total_carbohydrates",total_carbohydrates);
            intent.putExtra("total_protein",total_protein);
            intent.putExtra("total_potassium",total_potassium);

            startActivity(intent);
        });

        return view;
    }

    // 更新环形进度条
    private void updateProgressBar(int x) {
        // 计算进度百分比
        int progress = x; // 转换为百分比进度
        System.out.println("计算进度");
        // 在主线程中更新 UI
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("进度：");
                    System.out.println(progress);
                    circularProgressBar.setProgress(progress);  // 设置进度
                }
            });
        }
    }
    // 更新摄入千卡数的文本
    private void updateCaloriesText(int x) {
        // 更新TextView的文本，显示摄入的千卡数
        String caloriesText = "今日已摄入 " + x + " 千卡";
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    caloriesTextView.setText(caloriesText);  // 更新文本内容
                }
            });
        }
    }
public boolean istoday(String day) throws ParseException {
    // 定义日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 解析给定的日期字符串为 Date 对象
    Date givenDate = sdf.parse(day);

    // 获取今天的日期
    Date today = new Date();

    // 比较日期（只比较年月日，不考虑时分秒）
    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    String givenDateStr = dayFormat.format(givenDate);
    String todayStr = dayFormat.format(today);

    if (givenDateStr.equals(todayStr)) {
       return true;
    } else {
       return false;
    }
}
}

