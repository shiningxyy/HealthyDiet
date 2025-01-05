package com.example.healthydiet.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthydiet.R;
import com.example.healthydiet.entity.Comment;
import com.example.healthydiet.websocket.WebSocketManager;

import java.util.List;

public class CommentManageAdapter extends BaseAdapter {
    private List<Comment> commentList;
    private Context context;
    private WebSocketManager webSocketManager;

    public CommentManageAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentManageAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment_manage, parent, false);
            viewHolder = new CommentManageAdapter.ViewHolder();
            viewHolder.commentIdTextView = convertView.findViewById(R.id.commentIdTextView);
            viewHolder.contentTextView = convertView.findViewById(R.id.contentTextView);
            viewHolder.isBlockedTextView = convertView.findViewById(R.id.isBlockedTextView);
            viewHolder.block = convertView.findViewById(R.id.block);
            viewHolder.unblock = convertView.findViewById(R.id.unblock);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CommentManageAdapter.ViewHolder) convertView.getTag();
        }

        Comment comment = commentList.get(position);
        int comment_id=comment.getComment_id();
        int is_blocked=comment.getIs_offending();
        String content=comment.getComment_content();

        viewHolder.commentIdTextView.setText("评论ID:"+comment_id);
        if(is_blocked==0){
            viewHolder.isBlockedTextView.setText(" 封禁状态：正常");
        }
        else{
            viewHolder.isBlockedTextView.setText(" 封禁状态：封禁中");
        }
        viewHolder.contentTextView.setText("评论："+content);

        // Block button behavior
        viewHolder.block.setOnClickListener(v -> {
            if (is_blocked == 0) {
                // 显示封禁对话框
                new AlertDialog.Builder(context)
                        .setMessage("确定封禁该评论？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 发送封禁请求到后端
                            webSocketManager = WebSocketManager.getInstance();
                            webSocketManager.logConnectionStatus();  // 记录连接状态

                            // 确保WebSocket已连接后再发送请求
                            if (!webSocketManager.isConnected()) {
                                Log.d("CommentManageAdp", "WebSocket not connected, attempting to reconnect...");
                                webSocketManager.reconnect();
                            }
                            webSocketManager.sendMessage("offendComment:"+comment_id);

                            comment.setIs_offending(1);  // 更新状态
                            notifyDataSetChanged(); // 刷新列表
                        })
                        .setNegativeButton("取消", null)
                        .show();
            } else {

                Toast.makeText(context, "该评论处于封禁中", Toast.LENGTH_SHORT).show();
            }
        });

        // Unblock button behavior
        viewHolder.unblock.setOnClickListener(v -> {
            if (is_blocked == 0) {
                // 提示用户该用户已经处于正常状态
                Toast.makeText(context, "该评论处于正常状态", Toast.LENGTH_SHORT).show();
            } else {
                // 显示解封对话框
                new AlertDialog.Builder(context)
                        .setMessage("确定解封该评论？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 发送解封请求到后端
                            webSocketManager = WebSocketManager.getInstance();
                            webSocketManager.logConnectionStatus();  // 记录连接状态

                            // 确保WebSocket已连接后再发送请求
                            if (!webSocketManager.isConnected()) {
                                Log.d("CommentManageAdp", "WebSocket not connected, attempting to reconnect...");
                                webSocketManager.reconnect();
                            }
                            webSocketManager.sendMessage("unoffendComment:"+comment_id);
                            comment.setIs_offending(0);  // 更新用户状态
                            notifyDataSetChanged(); // 刷新列表
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        return convertView;
    }



    public static class ViewHolder {
        TextView commentIdTextView;
        TextView contentTextView;
        TextView isBlockedTextView;
        Button block;
        Button unblock;

    }
}
