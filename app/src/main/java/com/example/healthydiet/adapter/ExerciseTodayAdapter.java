package com.example.healthydiet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.R;
import com.example.healthydiet.websocket.WebSocketManager;

import java.util.List;

public class ExerciseTodayAdapter extends BaseAdapter {

    private Context context;
    private List<ExerciseRecord> exerciseRecords;
    Button deleteButton;
    public ExerciseTodayAdapter(Context context, List<ExerciseRecord> exerciseRecords) {
        this.context = context;
        this.exerciseRecords = exerciseRecords;
    }

    @Override
    public int getCount() {
        return exerciseRecords.size(); // 返回数据集合的大小
    }

    @Override
    public Object getItem(int position) {
        return exerciseRecords.get(position); // 返回当前项
    }

    @Override
    public long getItemId(int position) {
        return position; // 返回当前项的ID
    }

    // 获取每一项的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 使用 ViewHolder 模式优化性能
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exercise_record_today, parent, false);
            holder = new ViewHolder();
            holder.exerciseNameTextView = convertView.findViewById(R.id.exerciseNameTextView);
            holder.exerciseDurationTextView = convertView.findViewById(R.id.exerciseDurationTextView);
            holder.exerciseCaloriesTextView = convertView.findViewById(R.id.exerciseCaloriesTextView); // 新增
            holder.exerciseDateTextView = convertView.findViewById(R.id.exerciseDateTextView); // 新增

            convertView.setTag(holder);  // 保存 ViewHolder 到 convertView 中
        } else {
            holder = (ViewHolder) convertView.getTag();  // 从 convertView 中获取 ViewHolder
        }

        // 获取当前的 ExerciseRecord
        ExerciseRecord exerciseRecord = exerciseRecords.get(position);
        Log.d("ExerciseList", "message "+exerciseRecord.getexerciseName());

        String duration=(exerciseRecord.getDuration());

        String[] timeParts = duration.split(":"); // 分割字符串为 [小时, 分钟, 秒]

        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        // 将时间转换为分钟
        int durationtime = (hours * 60) + minutes + (seconds / 60);
        // 设置数据到视图中
        holder.exerciseNameTextView.setText(exerciseRecord.getexerciseName());
        holder.exerciseDurationTextView.setText(String.format("%d分钟", durationtime));
        holder.exerciseCaloriesTextView.setText(String.format("%d千卡", exerciseRecord.getBurnedCaloris()));
        holder.exerciseDateTextView.setText(exerciseRecord.getDate());

        return convertView;  // 返回当前项的视图
    }
    private void deleteExerciseRecord(ExerciseRecord record) {
        // 将删除记录的消息发送给服务器
        WebSocketManager webSocketManager = WebSocketManager.getInstance();
        String deleteMessage = "deleteExerciseRecord:" + record.getexerciseRecordId();  // 假设每个记录有一个唯一的 ID
        if (!webSocketManager.isConnected()) {
            webSocketManager.reconnect();
        }
        webSocketManager.sendMessage(deleteMessage);
    }
    // 内部 ViewHolder 类，提高性能
    static class ViewHolder {
        TextView exerciseNameTextView;
        TextView exerciseDurationTextView;
        TextView exerciseCaloriesTextView;  // 新增
        TextView exerciseDateTextView;  // 新增
    }
}
