package com.example.healthydiet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;


import com.example.healthydiet.activity.ExerciselistActivity;
import com.example.healthydiet.adapter.ExerciseHistoryAdapter;
import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.R;

import java.util.ArrayList;
import java.util.List;

public class HealthyFragment extends Fragment {

    private BarChart  exerciseTrendGraph;
    private ListView exerciseListView;  // 使用 ListView 替代 RecyclerView
    private Button goToExerciseSelectButton;
    private TextView todayExerciseTime, todayCaloriesBurned;
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();
    private ExerciseHistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_healthy, container, false);

        // 初始化视图组件
        exerciseTrendGraph = view.findViewById(R.id.exerciseTrendGraph);  // HelloChart 图表
        exerciseListView = view.findViewById(R.id.exerciseListView);  // 初始化 ListView
        goToExerciseSelectButton = view.findViewById(R.id.goToExerciseSelectButton);
        todayExerciseTime = view.findViewById(R.id.todayExerciseTime);
        todayCaloriesBurned = view.findViewById(R.id.todayCaloriesBurned);

        // 初始化 ListView 适配器
        adapter = new ExerciseHistoryAdapter(getContext(), exerciseRecords);  // 适配器要传递上下文
        exerciseListView.setAdapter(adapter);

        // 设置运动趋势图
        setupExerciseTrendGraph();

        // 设置今日运动信息
        updateTodayExerciseInfo();

        // 跳转到运动选择界面
        goToExerciseSelectButton.setOnClickListener(v -> onSelectExerciseClicked());

        return view;
    }

    // 设置运动趋势图（HelloChart）
    private void setupExerciseTrendGraph() {
        // 模拟数据
        ArrayList<BarEntry> entries = new ArrayList<>();


        // 创建数据集
        BarDataSet dataSet = new BarDataSet(entries, "运动时间 (分钟)");

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
