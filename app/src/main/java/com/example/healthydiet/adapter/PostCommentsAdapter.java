package com.example.healthydiet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.healthydiet.R;
import com.example.healthydiet.entity.Comment;
import com.example.healthydiet.entity.ExerciseRecord;
import com.example.healthydiet.websocket.WebSocketManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentsAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> commentList;
    Button deleteButton;
    public PostCommentsAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }
    // 更新数据的方法
    public void updateData(List<Comment> newCommentList) {
        this.commentList = newCommentList;
    }
    @Override
    public int getCount() {
        return commentList.size(); // 返回数据集合的大小
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position); // 返回当前项
    }

    @Override
    public long getItemId(int position) {
        return position; // 返回当前项的ID
    }

    // 获取每一项的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 使用 ViewHolder 模式优化性能
        PostCommentsAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            holder = new PostCommentsAdapter.ViewHolder();
            holder.commentTextView = convertView.findViewById(R.id.commentTextView);
            holder.profileImageView = convertView.findViewById(R.id.profileImageView);

            convertView.setTag(holder);  // 保存 ViewHolder 到 convertView 中
        } else {
            holder = (PostCommentsAdapter.ViewHolder) convertView.getTag();  // 从 convertView 中获取 ViewHolder
        }

        // 获取当前的 ExerciseRecord
        Comment comment = commentList.get(position);
        String content= comment.getComment_content();
        String profilePic=comment.getProfilePic();

        holder.commentTextView.setText(content);
        Glide.with(convertView)
                .load(profilePic)  // 加载用户头像的 URL
                .placeholder(R.drawable.ic_profile1)  // 加载中的占位图
                .error(R.drawable.avater)  // 加载失败时的图片
                .into(holder.profileImageView);  // 设置到头像视图

        return convertView;  // 返回当前项的视图
    }

    // 内部 ViewHolder 类，提高性能
    static class ViewHolder {
        TextView commentTextView;
        // 获取视图中的头像和昵称控件
        CircleImageView profileImageView;
    }
}
