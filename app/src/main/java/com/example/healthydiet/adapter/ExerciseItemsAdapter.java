package com.example.healthydiet.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.healthydiet.manager.UserManager;
import com.example.healthydiet.entity.ExerciseItem;
import com.example.healthydiet.R;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseItemsAdapter extends BaseAdapter {
    private List<ExerciseItem> exerciseItems;
    private Context context;
    private WebSocketManager webSocketManager;

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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
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
        dialog.setContentView(R.layout.card_exercise);
        EditText exerciseDurationEditText = dialog.findViewById(R.id.exerciseDurationEditText);

        Button yesButton = dialog.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
            webSocketManager = WebSocketManager.getInstance();
            User user = UserManager.getInstance().getUser();
            String durationStr = exerciseDurationEditText.getText().toString();
            int duration = 0;

            if (!durationStr.isEmpty()) {
                duration = Integer.parseInt(durationStr);  // 获取运动时长
            }
            int hours = duration / 60;

            // 计算剩余的分钟数
            int minutes = duration % 60;

            // 秒数固定为 0
            int seconds = 0;

            // 将结果格式化为 HH:mm:ss 形式
            String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            // 输出结果
            String addExerciseRecordMessage = "addExerciseRecord:{" +
                    "\"exerciseId\": \"" + exerciseItem.getExerciseId() + "\"," +
                    "\"duration\": \"" + formattedTime +"\"," +"\"date\": \"" + getCurrentTime() +
                    "\"" +"}";
            Log.d("MainActivity", "Sending login message: " + addExerciseRecordMessage);
            if (!webSocketManager.isConnected()) {
                webSocketManager.reconnect();
            }
            webSocketManager.sendMessage(addExerciseRecordMessage);

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
