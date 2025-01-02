package com.example.healthydiet.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthydiet.R;
import com.example.healthydiet.activity.PostDetailActivity;
import com.example.healthydiet.entity.FoodRecord;
import com.example.healthydiet.entity.Post;
import com.example.healthydiet.websocket.WebSocketManager;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostListViewHolder>{
    private List<Post> PostList;
    private WebSocketManager webSocketManager;

    // 构造器
    public PostListAdapter(List<Post> PostList) {
        this.PostList = PostList;
    }

    @NonNull
    @Override
    public PostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用布局填充器将每个食物记录的卡片视图转换为 View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListViewHolder holder, int position) {

        Post post = PostList.get(position);
        webSocketManager = WebSocketManager.getInstance();

        holder.title.setText(post.getPost_title());
        holder.tags.setText(post.getTags());
        String content = post.getPost_content();
        if (content.length() > 40) {
            content = content.substring(0, 40) + "...";  // 截取前40个字符并加上省略号
        }
        holder.content.setText(content);

        // 为按钮设置点击事件监听器
        holder.detailsButton.setOnClickListener(v -> {
            // 创建Intent跳转到新的Activity
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            // 将Post的ID或其他必要的数据传递给目标Activity
            intent.putExtra("post", post);  // 假设Post类有getPost_id()方法
            v.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return PostList.size();
    }

    // ViewHolder 用于缓存每个条目的视图
    public static class PostListViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView tags;
        TextView content;
        Button detailsButton;  // 为按钮添加引用
        public PostListViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            tags = itemView.findViewById(R.id.tags);
            content = itemView.findViewById(R.id.content);
            detailsButton = itemView.findViewById(R.id.details_button);  // 获取按钮的引用
        }
    }
}
