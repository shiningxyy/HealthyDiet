package com.example.healthydiet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.healthydiet.activity.AllExerciseRecordActivity;
import com.example.healthydiet.adapter.ExerciseTodayAdapter;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.example.healthydiet.manager.UserManager;

import com.example.healthydiet.activity.ExerciseListActivity;
import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthyFragment extends Fragment {

    private BarChart  exerciseTrendGraph;
    private LineChart weightTrendGraph;

    private ListView exerciseListView;  // 使用 ListView 替代 RecyclerView
    private Button goToExerciseSelectButton;
    private Button all_record_button;
    private TextView todayExerciseTime, todayCaloriesBurned,weekWeight,weekExercise;
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();
    private ExerciseTodayAdapter adapter;
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
        adapter = new ExerciseTodayAdapter(getContext(), exerciseRecords);  // 适配器传递上下文
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
                            exerciseJson.getInt("exerciseRecordId"),
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


        all_record_button= view.findViewById(R.id.allRecordButton);
        // 设置按钮点击事件，跳转到另一个 Activity
        all_record_button.setOnClickListener(v -> {
            // 使用 Intent 跳转到新的 Activity
            Intent intent = new Intent(getActivity(), AllExerciseRecordActivity.class); // 这里的 NewActivity 是你想跳转到的 Activity
            // 将 user 对象传递给目标 Activity
            //intent.putExtra("user", user); // 将 user 对象作为 Extra 传递
            startActivity(intent);
        });
        // 跳转到运动选择界面
        goToExerciseSelectButton.setOnClickListener(v -> onSelectExerciseClicked());

        return view;
    }

    private void updateExerciseListView(List<ExerciseRecord> exerciseRecordList) {
        // 使用新的数据更新适配器
// 获取今天的日期
        String todayDate = getCurrentDate();

        // 筛选出今天的运动记录
        List<ExerciseRecord> todayRecords = new ArrayList<>();
        for (ExerciseRecord record : exerciseRecordList) {
            if (record.getDate().equals(todayDate)) {
                todayRecords.add(record);
            }
        }

        adapter = new ExerciseTodayAdapter(getContext(), todayRecords);  // 适配器传递上下文
        exerciseListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // 设置运动趋势图
        setupExerciseTrendGraph(exerciseRecordList);

        // 设置今日运动信息
        updateTodayExerciseInfo(todayRecords);
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }

    private void setupExerciseTrendGraph(List<ExerciseRecord> exerciseRecordList) {
        // 获取当前日期并计算最近7天的日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        long currentTimeMillis = currentDate.getTime();
        long sevenDaysAgoMillis = currentTimeMillis - (7L * 24 * 60 * 60 * 1000);  // 7天前的时间戳
        Date sevenDaysAgoDate = new Date(sevenDaysAgoMillis);

        // 创建一个 List 来存储最近7天的日期（按日期顺序）
        List<String> recent7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Date date = new Date(currentTimeMillis - (i * 24 * 60 * 60 * 1000));
            recent7Days.add(dateFormat.format(date));  // 获取格式化后的日期
        }

        // 创建一个 Map 来存储每天的总消耗卡路里（如果没有数据，则为0）
        HashMap<String, Integer> dailyCaloriesMap = new HashMap<>();

        // 默认每天的消耗卡路里为 0
        for (String date : recent7Days) {
            dailyCaloriesMap.put(date, 0);
        }

        // 遍历所有记录并累加每天的消耗卡路里
        for (ExerciseRecord record : exerciseRecordList) {
            String date = record.getDate();  // 获取日期
            try {
                Date recordDate = dateFormat.parse(date);  // 将日期字符串转换为 Date 对象
                if (recordDate.after(sevenDaysAgoDate) || recordDate.equals(sevenDaysAgoDate)) {
                    // 假设每分钟消耗一定的卡路里（例如：每分钟消耗5卡）
                    String duration = record.getDuration();  // 获取运动时长（格式：HH:mm:ss）
                    String[] timeParts = duration.split(":");  // 分割字符串为 [小时, 分钟, 秒]

                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    int seconds = Integer.parseInt(timeParts[2]);

                    // 将时间转换为分钟
                    int totalMinutes = (hours * 60) + minutes + (seconds / 60);

                    // 计算消耗的卡路里
                    int caloriesBurned = totalMinutes * 5;  // 假设每分钟消耗5卡

                    // 累加该日期的消耗卡路里
                    if (dailyCaloriesMap.containsKey(date)) {
                        int existingCalories = dailyCaloriesMap.get(date);
                        dailyCaloriesMap.put(date, existingCalories + caloriesBurned);
                    } else {
                        dailyCaloriesMap.put(date, caloriesBurned);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 将 Map 转换为 List 以便排序
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(dailyCaloriesMap.entrySet());

        // 使用比较器按日期排序
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                try {
                    Date date1 = dateFormat.parse(entry1.getKey());
                    Date date2 = dateFormat.parse(entry2.getKey());
                    return date1.compareTo(date2);  // 按日期排序
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        // 创建一个 ArrayList 来存放 BarEntry
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();

        // 将排序后的数据填充到 BarEntry 和 X 轴标签中
        int index = 0;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String date = entry.getKey();  // 日期
            int totalCalories = entry.getValue();  // 总消耗卡路里

            // 将 x 轴的索引值设置为当前索引，y 轴为总消耗卡路里
            entries.add(new BarEntry(index, totalCalories));

            // 将日期添加到 X 轴标签
            xLabels.add(date);
            index++;
        }

        // 创建数据集
        BarDataSet dataSet = new BarDataSet(entries, "消耗卡路里 (卡)");
        dataSet.setColor(getResources().getColor(android.R.color.holo_green_light));  // 设置条形图的颜色

        // 创建 BarData
        BarData barData = new BarData(dataSet);

        // 设置数据给 BarChart
        exerciseTrendGraph.setData(barData);

        // 设置图表的描述标题
        exerciseTrendGraph.getDescription().setEnabled(true);
        exerciseTrendGraph.getDescription().setText("一周运动情况");
        exerciseTrendGraph.getDescription().setTextSize(14f);
        exerciseTrendGraph.getDescription().setPosition(0f, 1f);  // 设置标题的位置

        // 设置图表的 X 轴标签
        XAxis xAxis = exerciseTrendGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));  // 设置 X 轴的标签
        xAxis.setGranularity(1f);  // 强制显示每一天的刻度

        // 设置 Y 轴
        YAxis leftAxis = exerciseTrendGraph.getAxisLeft();
        leftAxis.setAxisMinimum(0f);  // 设置最小值为0
        leftAxis.setDrawLabels(true);  // 显示左侧的刻度标签

        // 隐藏右侧 Y 轴
        exerciseTrendGraph.getAxisRight().setEnabled(false);

        // 设置图表背景网格
        exerciseTrendGraph.setDrawGridBackground(false);  // 禁用背景网格

        // 刷新图表
        exerciseTrendGraph.invalidate();
    }
//    private void setupWeightTrendGraph(List<WeightRecord> weightRecordList) {
//        // 获取当前日期并计算最近7天的日期
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date currentDate = new Date();
//        long currentTimeMillis = currentDate.getTime();
//        long sevenDaysAgoMillis = currentTimeMillis - (7L * 24 * 60 * 60 * 1000);  // 7天前的时间戳
//        Date sevenDaysAgoDate = new Date(sevenDaysAgoMillis);
//
//        // 创建一个 List 来存储最近7天的日期（按日期顺序）
//        List<String> recent7Days = new ArrayList<>();
//        for (int i = 6; i >= 0; i--) {
//            Date date = new Date(currentTimeMillis - (i * 24 * 60 * 60 * 1000));
//            recent7Days.add(dateFormat.format(date));  // 获取格式化后的日期
//        }
//
//        // 创建一个 Map 来存储每天的总消耗卡路里（如果没有数据，则为0）
//        HashMap<String, Integer> dailyCaloriesMap = new HashMap<>();
//
//        // 默认每天的消耗卡路里为 0
//        for (String date : recent7Days) {
//            dailyCaloriesMap.put(date, 0);
//        }
//
//        // 遍历所有记录并累加每天的消耗卡路里
//        for (WeightRecord record : weightRecordList) {
//            String date = record.getDate();  // 获取日期
//            try {
//                Date recordDate = dateFormat.parse(date);  // 将日期字符串转换为 Date 对象
//                if (recordDate.after(sevenDaysAgoDate) || recordDate.equals(sevenDaysAgoDate)) {
//                    // 假设每分钟消耗一定的卡路里（例如：每分钟消耗5卡）
//                    String duration = record.getDuration();  // 获取运动时长（格式：HH:mm:ss）
//                    String[] timeParts = duration.split(":");  // 分割字符串为 [小时, 分钟, 秒]
//
//                    int hours = Integer.parseInt(timeParts[0]);
//                    int minutes = Integer.parseInt(timeParts[1]);
//                    int seconds = Integer.parseInt(timeParts[2]);
//
//                    // 将时间转换为分钟
//                    int totalMinutes = (hours * 60) + minutes + (seconds / 60);
//
//                    // 计算消耗的卡路里
//                    int caloriesBurned = totalMinutes * 5;  // 假设每分钟消耗5卡
//
//                    // 累加该日期的消耗卡路里
//                    if (dailyCaloriesMap.containsKey(date)) {
//                        int existingCalories = dailyCaloriesMap.get(date);
//                        dailyCaloriesMap.put(date, existingCalories + caloriesBurned);
//                    } else {
//                        dailyCaloriesMap.put(date, caloriesBurned);
//                    }
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // 将 Map 转换为 List 以便排序
//        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(dailyCaloriesMap.entrySet());
//
//        // 使用比较器按日期排序
//        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
//            @Override
//            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
//                try {
//                    Date date1 = dateFormat.parse(entry1.getKey());
//                    Date date2 = dateFormat.parse(entry2.getKey());
//                    return date1.compareTo(date2);  // 按日期排序
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                return 0;
//            }
//        });
//
//        // 创建一个 ArrayList 来存放 BarEntry
//        ArrayList<BarEntry> entries = new ArrayList<>();
//        ArrayList<String> xLabels = new ArrayList<>();
//
//        // 将排序后的数据填充到 BarEntry 和 X 轴标签中
//        int index = 0;
//        for (Map.Entry<String, Integer> entry : sortedEntries) {
//            String date = entry.getKey();  // 日期
//            int totalCalories = entry.getValue();  // 总消耗卡路里
//
//            // 将 x 轴的索引值设置为当前索引，y 轴为总消耗卡路里
//            entries.add(new BarEntry(index, totalCalories));
//
//            // 将日期添加到 X 轴标签
//            xLabels.add(date);
//            index++;
//        }
//
//        // 创建数据集
//        BarDataSet dataSet = new BarDataSet(entries, "消耗卡路里 (卡)");
//        dataSet.setColor(getResources().getColor(android.R.color.holo_green_light));  // 设置条形图的颜色
//
//        // 创建 BarData
//        BarData barData = new BarData(dataSet);
//
//        // 设置数据给 BarChart
//        exerciseTrendGraph.setData(barData);
//
//        // 设置图表的描述标题
//        exerciseTrendGraph.getDescription().setEnabled(true);
//        exerciseTrendGraph.getDescription().setText("一周运动情况");
//        exerciseTrendGraph.getDescription().setTextSize(14f);
//        exerciseTrendGraph.getDescription().setPosition(0f, 1f);  // 设置标题的位置
//
//        // 设置图表的 X 轴标签
//        XAxis xAxis = exerciseTrendGraph.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));  // 设置 X 轴的标签
//        xAxis.setGranularity(1f);  // 强制显示每一天的刻度
//
//        // 设置 Y 轴
//        YAxis leftAxis = exerciseTrendGraph.getAxisLeft();
//        leftAxis.setAxisMinimum(0f);  // 设置最小值为0
//        leftAxis.setDrawLabels(true);  // 显示左侧的刻度标签
//
//        // 隐藏右侧 Y 轴
//        exerciseTrendGraph.getAxisRight().setEnabled(false);
//
//        // 设置图表背景网格
//        exerciseTrendGraph.setDrawGridBackground(false);  // 禁用背景网格
//
//        // 刷新图表
//        exerciseTrendGraph.invalidate();
//    }


    // 获取今天日期的方法
    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());  // 获取今天的日期，格式如 "2024-12-25"
    }

    // 更新今日运动信息
    private void updateTodayExerciseInfo(List<ExerciseRecord> allRecords) {
        float totalExerciseTime = 0;
        float totalCaloriesBurned = 0;

        // 遍历所有运动记录，筛选出今日的记录并累加
        for (ExerciseRecord record : allRecords) {
            String duration=record.getDuration();
            String[] timeParts = duration.split(":"); // 分割字符串为 [小时, 分钟, 秒]

            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            // 将时间转换为分钟
            int durationtime = (hours * 60) + minutes + (seconds / 60);
                totalExerciseTime += durationtime; // 累加今日的运动时长
                totalCaloriesBurned += record.getBurnedCaloris();  // 累加今日的消耗热量
        }

        // 更新UI
        todayExerciseTime.setText("今日运动时间: " + totalExerciseTime + "分钟");
        todayCaloriesBurned.setText("消耗热量: " + totalCaloriesBurned + "千卡");
    }

    // 跳转到运动项目选择界面
    private void onSelectExerciseClicked() {
        Intent intent = new Intent(getActivity(), ExerciseListActivity.class);
        startActivity(intent);
    }
}
