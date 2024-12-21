package com.example.healthydiet.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;

import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.ViewHolder> {
    private List<String> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;  // 默认没有选中项
    private int itemHeight;  // 每个item的高度


    public SidebarAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 使用简单的列表项布局
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String category = categories.get(position);
        holder.textView.setText(category);
        // 设置每个 item 的高度
        if (itemHeight > 0) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = itemHeight;  // 设置每个 item 的高度为计算出来的高度
            holder.itemView.setLayoutParams(params);
        }
        // 如果当前项未选中，背景为绿色；如果选中，背景为白色
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_item_color));  // 选中项背景为白色
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.unselected_item_color));  // 未选中项背景为绿色
        }

        holder.itemView.setOnClickListener(v -> {
            // 更新选中项位置
            selectedPosition = position;
            notifyDataSetChanged();  // 刷新整个列表
            listener.onCategoryClicked(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    public void setItemHeight(int height) {
        this.itemHeight = height;
        notifyDataSetChanged();  // 更新 item 高度
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClicked(String category);
    }
}
