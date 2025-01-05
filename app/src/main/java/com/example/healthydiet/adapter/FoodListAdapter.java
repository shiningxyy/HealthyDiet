package com.example.healthydiet.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;
import com.example.healthydiet.activity.AddFoodRecordActivity;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
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
        dialog.setContentView(R.layout.card_food);

        // 设置卡片中的文本
        TextView foodNameTextView = dialog.findViewById(R.id.foodNameTextView);
        foodNameTextView.setText(foodItem.getName()+"/100克");

        TextView caloriesTextView = dialog.findViewById(R.id.caloriesTextView);
        caloriesTextView.setText("热量："+foodItem.getCalories()+ "千卡");

        TextView fatTextView = dialog.findViewById(R.id.fatTextView);
        fatTextView.setText("脂肪："+String.format("%.1f", foodItem.getFat())+"克");

        TextView proteinTextView = dialog.findViewById(R.id.proteinTextView);
        proteinTextView.setText("蛋白质："+String.format("%.1f", foodItem.getProtein())+"克");

        TextView carbohydratesTextView = dialog.findViewById(R.id.carbohydratesTextView);
        carbohydratesTextView.setText("碳水化合物："+String.format("%.1f", foodItem.getCarbohydrates())+"克");

        TextView dietaryFiberTextView = dialog.findViewById(R.id.dietaryFiberTextView);
        dietaryFiberTextView.setText("膳食纤维："+String.format("%.1f", foodItem.getDietaryFiber())+"克");

        TextView potassiumTextView = dialog.findViewById(R.id.potassiumTextView);
        potassiumTextView.setText("钾："+String.format("%.1f", foodItem.getPotassium())+"毫克");

        TextView sodiumTextView = dialog.findViewById(R.id.sodiumTextView);
        sodiumTextView.setText("钠："+String.format("%.1f", foodItem.getSodium())+"毫克");


        // 设置“是”按钮
        Button yesButton = dialog.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(v -> {
            // 跳转到 FoodRecord 页面
            Intent intent = new Intent(context, AddFoodRecordActivity.class);
            // 你可以将 foodItem 的相关数据传递到 FoodRecordActivity
            intent.putExtra("food_name", foodItem.getName());
            intent.putExtra("food_calories", foodItem.getCalories());
            intent.putExtra("food_fat", foodItem.getFat());
            intent.putExtra("food_protein", foodItem.getProtein());
            intent.putExtra("food_carbohydrates", foodItem.getCarbohydrates());
            intent.putExtra("food_id", foodItem.getFoodid());
            intent.putExtra("food_DietaryFiber", foodItem.getDietaryFiber());
            intent.putExtra("food_Potassium", foodItem.getPotassium());
            intent.putExtra("food_Sodium", foodItem.getSodium());
            // 可以根据需求传递更多数据
            context.startActivity(intent);
            dialog.dismiss();  // 关闭 Dialog
        });

        // 设置“否”按钮
        Button noButton = dialog.findViewById(R.id.noButton);
        noButton.setOnClickListener(v -> {
            // 关闭 Dialog
            dialog.dismiss();
        });
        // 显示Dialog
        dialog.show();
    }
}
