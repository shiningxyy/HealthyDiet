package com.example.healthydiet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.R;

import java.util.List;

public class ExerciseHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<ExerciseRecord> exerciseRecords;

    public ExerciseHistoryAdapter(Context context, List<ExerciseRecord> exerciseRecords) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.exercise_record_item, parent, false);
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

        // 设置数据到视图中
        holder.exerciseNameTextView.setText(exerciseRecord.getExerciseId());
        holder.exerciseDurationTextView.setText(String.format("%d分钟", exerciseRecord.getDuration()));
        holder.exerciseCaloriesTextView.setText(String.format("%d千卡", exerciseRecord.getBurnedCaloris()));
        holder.exerciseDateTextView.setText(exerciseRecord.getDate());

        return convertView;  // 返回当前项的视图
    }

    // 内部 ViewHolder 类，提高性能
    static class ViewHolder {
        TextView exerciseNameTextView;
        TextView exerciseDurationTextView;
        TextView exerciseCaloriesTextView;  // 新增
        TextView exerciseDateTextView;  // 新增
    }
}
