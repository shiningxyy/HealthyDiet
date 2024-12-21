package com.example.healthydiet.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.FoodItem;

import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
    private List<FoodItem> foodItems;
    private Context context; // 用于弹出 Dialog
    public FoodListAdapter(List<FoodItem> foodItems, Context context) {
        this.foodItems = foodItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodNameTextView.setText(foodItem.getName());
        holder.caloriesTextView.setText(foodItem.getCalories() + "千卡/100克");
        holder.itemView.setOnClickListener(v -> showFoodPopup(foodItem));
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView caloriesTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.food_name);
            caloriesTextView = itemView.findViewById(R.id.food_calories);
        }
    }

    // 弹出食物名称卡片的 Dialog
    private void showFoodPopup(FoodItem foodItem) {
        // 创建Dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.card_popup);

        // 设置卡片中的文本
        TextView foodNameTextView = dialog.findViewById(R.id.foodNameTextView);
        foodNameTextView.setText(foodItem.getName());

        TextView caloriesTextView = dialog.findViewById(R.id.caloriesTextView);
        caloriesTextView.setText(foodItem.getCalories()+ "千卡/100克");

        TextView carbohydratesTextView = dialog.findViewById(R.id.carbohydratesTextView);
        carbohydratesTextView.setText(String.format("%.1f", foodItem.getCarbohydrates())+"克/100克");

        TextView potassiumTextView = dialog.findViewById(R.id.potassiumTextView);
        potassiumTextView.setText(String.format("%.1f", foodItem.getPotassium())+"毫克/100克");

        TextView sodiumTextView = dialog.findViewById(R.id.sodiumTextView);
        sodiumTextView.setText(String.format("%.1f", foodItem.getSodium())+"毫克/100克");

        TextView dietaryFiberTextView = dialog.findViewById(R.id.dietaryFiberTextView);
        dietaryFiberTextView.setText(String.format("%.1f", foodItem.getDietaryFiber())+"克/100克");
        // 显示Dialog
        dialog.show();
    }
}
