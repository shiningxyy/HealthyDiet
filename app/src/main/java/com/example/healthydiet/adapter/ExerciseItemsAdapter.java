package com.example.healthydiet.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.healthydiet.activity.HomeActivity;
import com.example.healthydiet.activity.MainActivity;
import com.example.healthydiet.entity.ExerciseItem;
import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Intent;

public class ExerciseItemsAdapter extends BaseAdapter {
    private List<ExerciseItem> exerciseItems;
    private Context context;

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
            convertView = LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.exerciseNameTextView = convertView.findViewById(R.id.exerciseNameTextView);
            viewHolder.caloriesTextView = convertView.findViewById(R.id.exerciseCaloriesTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ExerciseItem exerciseItem = exerciseItems.get(position);
        viewHolder.exerciseNameTextView.setText(exerciseItem.getName());
        viewHolder.caloriesTextView.setText(exerciseItem.getCaloriesPerHour() + "千卡/60分钟");

        // 点击事件，弹出详情
        convertView.setOnClickListener(v -> showExercisePopup(exerciseItem));

        return convertView;
    }

    // 获取当前时间
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }

    // 弹出运动详情卡片的 Dialog
    private void showExercisePopup(ExerciseItem exerciseItem) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.exercise_card);
        EditText exerciseDurationEditText = dialog.findViewById(R.id.exerciseDurationEditText);

        Button yesButton = dialog.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
//            String durationStr = exerciseDurationEditText.getText().toString();
//            int duration = 0;
//
//            if (!durationStr.isEmpty()) {
//                duration = Integer.parseInt(durationStr);  // 获取运动时长
//            }
//
//            // 计算消耗的卡路里
//            int burnedCalories = (int) ((exerciseItem.getCaloriesPerHour() / 60) * duration);
//
//            // 跳转到 FoodRecord 页面
//            Intent intent = new Intent(context, HomeActivity.class);
//            // 你可以将 foodItem 的相关数据传递到 FoodRecordActivity
//            intent.putExtra("exerciseId", exerciseItem.getExerciseId());
//            intent.putExtra("caloriesPerHour", exerciseItem.getCaloriesPerHour());
//            intent.putExtra("name", exerciseItem.getName());
//            intent.putExtra("duration", duration);
//            intent.putExtra("burnedCalories", burnedCalories);
//            intent.putExtra("date", getCurrentTime());
//
//            // 可以根据需求传递更多数据
//            context.startActivity(intent);
            dialog.dismiss();  // 关闭 Dialog

        });

        Button noButton = dialog.findViewById(R.id.noButton);
        noButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    public static class ViewHolder {
        TextView exerciseNameTextView;
        TextView caloriesTextView;
    }
}
