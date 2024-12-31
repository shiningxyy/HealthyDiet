package com.example.healthydiet.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.healthydiet.R;
import com.example.healthydiet.entity.FoodItem;
import com.example.healthydiet.entity.FoodRecord;
import com.example.healthydiet.websocket.WebSocketManager;
import com.example.healthydiet.websocket.WebSocketMessageType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodRecordAdapter extends RecyclerView.Adapter<FoodRecordAdapter.FoodRecordViewHolder> {

    private List<FoodRecord> foodRecordList;
    private WebSocketManager webSocketManager;

    // 构造器
    public FoodRecordAdapter(List<FoodRecord> foodRecordList) {
        this.foodRecordList = foodRecordList;
    }

    @NonNull
    @Override
    public FoodRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用布局填充器将每个食物记录的卡片视图转换为 View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_record, parent, false);
        return new FoodRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodRecordViewHolder holder, int position) {
        // 获取当前食物记录对象
        FoodRecord foodRecord = foodRecordList.get(position);
        webSocketManager = WebSocketManager.getInstance();

        // 在卡片视图中设置数据
        holder.recordTimeTextView.setText(foodRecord.getRecordTime());
        holder.foodNameTextView.setText(foodRecord.getFoodName());
        holder.foodWeightTextView.setText("食物重量：" + foodRecord.getFoodWeight() + "克");
        holder.caloriesTextView.setText("卡路里：" + foodRecord.getCalories() + " 千卡");
        holder.fatTextView.setText("脂肪：" + foodRecord.getFat() + " 克");
        holder.proteinTextView.setText("蛋白质：" + foodRecord.getProtein() + " 克");
        holder.carbohydratesTextView.setText("Carbs: " + foodRecord.getCarbohydrates() + " 克");
        holder.sodiumTextView.setText("钠：" + foodRecord.getSodium() + " 毫克");
        holder.potassiumTextView.setText("钾：" + foodRecord.getPotassium() + " 毫克");
        holder.dietaryFiberTextView.setText("膳食纤维：" + foodRecord.getDietaryFiber() + " 克");
        holder.deleteButton.setOnClickListener(v -> {
            // 删除当前记录
            removeItem(position);
        });
    }
    private void removeItem(int position) {
        // 从数据列表中移除该项
        FoodRecord foodRecord = foodRecordList.get(position);

        foodRecordList.remove(position);
        // 通知适配器数据已更改
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, foodRecordList.size());

        String deleteMessage = "deleteFoodRecord:" + foodRecord.getFoodRecordId(); // 根据实际需求构建消息

        // 使用 WebSocketManager 发送删除请求
        if (webSocketManager.isConnected()) {
            webSocketManager.sendMessage(deleteMessage);
            Log.d("FoodRecordAdapter", "Sent delete request to server: " + deleteMessage);
        } else {
            Log.e("FoodRecordAdapter", "WebSocket is not connected.");
        }
    }
    @Override
    public int getItemCount() {
        return foodRecordList.size();
    }

    // ViewHolder 用于缓存每个条目的视图
    public static class FoodRecordViewHolder extends RecyclerView.ViewHolder {

        TextView recordTimeTextView;
        TextView foodNameTextView;
        TextView foodWeightTextView;
        TextView caloriesTextView;
        TextView fatTextView;
        TextView proteinTextView;
        TextView carbohydratesTextView;
        TextView sodiumTextView;
        TextView potassiumTextView;
        TextView dietaryFiberTextView;
        Button deleteButton; // 新增删除按钮的引用

        public FoodRecordViewHolder(View itemView) {
            super(itemView);
            recordTimeTextView = itemView.findViewById(R.id.recordTimeTextView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            foodWeightTextView = itemView.findViewById(R.id.foodWeightTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);
            fatTextView = itemView.findViewById(R.id.fatTextView);
            proteinTextView = itemView.findViewById(R.id.proteinTextView);
            carbohydratesTextView = itemView.findViewById(R.id.carbohydratesTextView);
            sodiumTextView = itemView.findViewById(R.id.sodiumTextView);
            potassiumTextView = itemView.findViewById(R.id.potassiumTextView);
            dietaryFiberTextView = itemView.findViewById(R.id.dietaryFiberTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton); // 绑定删除按钮

        }
    }
}
