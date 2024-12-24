package com.example.healthydiet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.healthydiet.adapter.ExerciseItemsAdapter;
import com.example.healthydiet.entity.ExerciseItem;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.example.healthydiet.UserManager;

import com.example.healthydiet.activity.ExerciselistActivity;
import com.example.healthydiet.adapter.ExerciseHistoryAdapter;
import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HealthyFragment extends Fragment {

    private BarChart  exerciseTrendGraph;
    private ListView exerciseListView;  // 使用 ListView 替代 RecyclerView
    private Button goToExerciseSelectButton;
    private TextView todayExerciseTime, todayCaloriesBurned;
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();
    private ExerciseHistoryAdapter adapter;

    private WebSocketManager webSocketManager;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            exerciseId = getArguments().getInt("exerciseId");
//            caloriesPerHour = getArguments().getInt("caloriesPerHour");
//            name = getArguments().getString("name");
//            duration = getArguments().getString("duration");
//            burnedCalories = getArguments().getInt("burnedCalories");
//            date = getArguments().getString("date");
//            User user = UserManager.getInstance().getUser();
//            newexerciseRecord = new ExerciseRecord(exerciseId, user.getUserId(),date, duration, burnedCalories);
//            exerciseRecords.add(newexerciseRecord);
//        }
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        webSocketManager = WebSocketManager.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_healthy, container, false);
        // 初始化视图组件
        exerciseTrendGraph = view.findViewById(R.id.exerciseTrendGraph);  // HelloChart 图表
        exerciseListView = view.findViewById(R.id.exerciseListView);  // 初始化 ListView
        goToExerciseSelectButton = view.findViewById(R.id.goToExerciseSelectButton);
        todayExerciseTime = view.findViewById(R.id.todayExerciseTime);
        todayCaloriesBurned = view.findViewById(R.id.todayCaloriesBurned);

        // 初始化 ListView 适配器
        adapter = new ExerciseHistoryAdapter(getContext(), exerciseRecords);  // 适配器传递上下文
        exerciseListView.setAdapter(adapter);
        User user = UserManager.getInstance().getUser();
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
                getActivity().runOnUiThread(() -> updateExerciseListView(exerciseRecordList));

            } catch (Exception e) {
                Log.e("ExerciseList", "Error processing exercise list: " + e.getMessage());
                e.printStackTrace();
            }
        });

        String getUserExerciseRecordMessage = "getUserExerciseRecord:" +user.getUserId();
        Log.d("ExerciseList", "Sending ExerciseList message: " + getUserExerciseRecordMessage);
        if (!webSocketManager.isConnected()) {
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage(getUserExerciseRecordMessage);


        // 设置运动趋势图
        setupExerciseTrendGraph();

        // 设置今日运动信息
        updateTodayExerciseInfo();

        // 跳转到运动选择界面
        goToExerciseSelectButton.setOnClickListener(v -> onSelectExerciseClicked());

        return view;
    }

    private void updateExerciseListView(List<ExerciseRecord> exerciseRecordList) {
        // 使用新的数据更新适配器

        adapter = new ExerciseHistoryAdapter(getContext(), exerciseRecordList);  // 适配器传递上下文
        exerciseListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    private void setupExerciseTrendGraph() {
        // 模拟数据
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 10));  // 第一个数据点: x=0, y=10
        entries.add(new BarEntry(1, 20));  // 第二个数据点: x=1, y=20
        entries.add(new BarEntry(2, 30));  // 第三个数据点: x=2, y=30

        // 创建数据集
        BarDataSet dataSet = new BarDataSet(entries, "运动时间 (分钟)");
        dataSet.setColor(getResources().getColor(android.R.color.holo_green_light));  // 使用预定义的绿色

        // 创建 BarData
        BarData barData = new BarData(dataSet);

        // 设置数据给 BarChart
        exerciseTrendGraph.setData(barData);

        // 刷新图表
        exerciseTrendGraph.invalidate();
    }

    // 更新今日运动信息
    private void updateTodayExerciseInfo() {
        todayExerciseTime.setText("今日运动时间: 60分钟");
        todayCaloriesBurned.setText("消耗热量: 500千卡");
    }

    // 跳转到运动项目选择界面
    private void onSelectExerciseClicked() {
        Intent intent = new Intent(getActivity(), ExerciselistActivity.class);
        startActivity(intent);
    }
}
