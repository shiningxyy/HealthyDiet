package com.example.healthydiet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.healthydiet.R;
import com.example.healthydiet.activity.FoodlistActivity;
import com.example.healthydiet.activity.MainActivity;
import com.example.healthydiet.activity.RegisterActivity;

// DietFragment.java
public class DietFragment extends Fragment {
    private ProgressBar circularProgressBar;
    private TextView caloriesTextView;  // 用来显示摄入千卡数
    private Button breakfast_button;
    private Button lunch_button;
    private Button dinner_button;
    public DietFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);

        // 初始化进度条
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        caloriesTextView = view.findViewById(R.id.caloriesTextView);

        // 模拟从后端获取的 x 值（可以替换为实际的后端数据）
        int x = 500; // 示例值，从后端获取的值

        // 更新进度条
        updateProgressBar(x);
        updateCaloriesText(x);

        // 初始化跳转按钮
        breakfast_button = view.findViewById(R.id.breakfastButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        breakfast_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), FoodlistActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
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
}

