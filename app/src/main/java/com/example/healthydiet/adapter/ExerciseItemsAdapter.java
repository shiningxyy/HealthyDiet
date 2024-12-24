package com.example.healthydiet.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.ExerciseItem;
import com.example.healthydiet.entity.ExerciseRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseItemsAdapter extends BaseAdapter {
    private List<ExerciseItem> exerciseItems;
    private Context context; // 用于弹出 Dialog

    public ExerciseItemsAdapter(List<ExerciseItem> exerciseItems, Context context) {
        this.exerciseItems = exerciseItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return exerciseItems.size();
    }

    @Override
    public Object getItem(int position) {
        return exerciseItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // Inflate the list item layout
            convertView = LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.exerciseNameTextView = convertView.findViewById(R.id.exerciseNameTextView);
            viewHolder.caloriesTextView = convertView.findViewById(R.id.exerciseCaloriesTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ExerciseItem exerciseItem = exerciseItems.get(position);
        viewHolder.exerciseNameTextView.setText(exerciseItem.getName());  // 设置运动名称
        viewHolder.caloriesTextView.setText(exerciseItem.getCaloriesPerHour() + "千卡/60分钟");  // 设置运动的卡路里消耗

        // 设置点击事件，弹出详情
        convertView.setOnClickListener(v -> showExercisePopup(exerciseItem));

        return convertView;
    }

    // 获取当前时间
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);  // 返回当前时间的字符串
    }

    public static class ViewHolder {
        TextView exerciseNameTextView;
        TextView caloriesTextView;
    }

    // 弹出运动详情卡片的 Dialog
    private void showExercisePopup(ExerciseItem exerciseItem) {
        // 创建 Dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.exercise_card);  // 自定义布局
        EditText exerciseDurationEditText = dialog.findViewById(R.id.exerciseDurationEditText);

        // 设置“确认”按钮
        Button yesButton = dialog.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
            String durationStr = exerciseDurationEditText.getText().toString();
            int duration = 0;

            if (!durationStr.isEmpty()) {
                duration = Integer.parseInt(durationStr);  // 转换为整数
            }
            // 获取选中的运动项名称
            String exerciseName = exerciseItem.getName();  // 假设 exerciseItem 是你选择的运动项
            // 获取当前时间，作为记录时间
            String currentTime = getCurrentTime();  // 方法获取当前时间

            // 创建新的运动记录
            ExerciseRecord exerciseRecord = new ExerciseRecord(exerciseName, duration, currentTime);

            // 将记录添加到记录列表
            addExerciseRecordToList(exerciseRecord);  // 添加记录的方法

            // 关闭 Dialog
            dialog.dismiss();
        });

        // 设置“取消”按钮
        Button noButton = dialog.findViewById(R.id.noButton);
        noButton.setOnClickListener(v -> {
            // 关闭 Dialog
            dialog.dismiss();
        });

        // 显示 Dialog
        dialog.show();
    }
}
