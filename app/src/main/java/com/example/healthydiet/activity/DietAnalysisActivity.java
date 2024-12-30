package com.example.healthydiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.FoodRecord;

import java.util.List;

public class DietAnalysisActivity extends AppCompatActivity {
    private List<FoodRecord> TodayRecordList;
    private int total_calories;
    private double total_fat=0;
    private double total_protein=0;
    private double total_carbohydrates=0;
    private double total_sodium=0;
    private double total_potassium=0;
    private double total_dietaryFiber=0;
    private ProgressBar progressBar;
    private TextView caloriesTextView;  // 用来显示摄入千卡数
    private TableLayout tableLayout;
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
        System.out.println(total_fat);

        // 初始化进度条
        progressBar = findViewById(R.id.progress_bar);

        // 设置进度条的最大值和当前进度
        progressBar.setMax(2000);  // 设置最大进度为 2000
        progressBar.setProgress(total_calories);  // 将 total_calories 作为当前进度

        caloriesTextView = findViewById(R.id.TextView2);
        String caloriesText = "今日已摄入 " + total_calories + " 千卡";
        caloriesTextView.setText(caloriesText);

        tableLayout = findViewById(R.id.table_layout);  // 获取 TableLayout
        addTableRow("项目","摄入量");
        addTableRow("脂肪",total_fat+"克");
        addTableRow("蛋白质",total_protein+"克");
        addTableRow("碳水化合物",total_carbohydrates+"克");
        addTableRow("膳食纤维",total_dietaryFiber+"克");
        addTableRow("钠",total_sodium+"毫克");
        addTableRow("钾",total_potassium+"毫克");
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
}
