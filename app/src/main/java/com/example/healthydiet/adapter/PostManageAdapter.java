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
import com.example.healthydiet.entity.Post;
import com.example.healthydiet.websocket.WebSocketManager;

import java.util.List;

public class PostManageAdapter extends BaseAdapter {
    private List<Post> postList;
    private Context context;
    private WebSocketManager webSocketManager;

    public PostManageAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostManageAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post_manage, parent, false);
            viewHolder = new PostManageAdapter.ViewHolder();
            viewHolder.postIdTextView = convertView.findViewById(R.id.postIdTextView);
            viewHolder.postTitleTextView = convertView.findViewById(R.id.postTitleTextView);
            viewHolder.isBlockedTextView = convertView.findViewById(R.id.isBlockedTextView);
            viewHolder.block = convertView.findViewById(R.id.block);
            viewHolder.unblock = convertView.findViewById(R.id.unblock);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PostManageAdapter.ViewHolder) convertView.getTag();
        }

        Post post = postList.get(position);
        int post_id=post.getPost_id();
        int is_blocked=post.getIs_offending();
        String title=post.getPost_title();

        viewHolder.postIdTextView.setText("帖子ID:"+post_id);
        if(is_blocked==0){
            viewHolder.isBlockedTextView.setText(" 封禁状态：正常");
        }
        else{
            viewHolder.isBlockedTextView.setText(" 封禁状态：封禁中");
        }
        viewHolder.postTitleTextView.setText("标题："+title);

        // Block button behavior
        viewHolder.block.setOnClickListener(v -> {
            if (is_blocked == 0) {
                // 显示封禁对话框
                new AlertDialog.Builder(context)
                        .setMessage("确定封禁该帖子？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 发送封禁请求到后端
                            webSocketManager = WebSocketManager.getInstance();
                            webSocketManager.logConnectionStatus();  // 记录连接状态

                            // 确保WebSocket已连接后再发送请求
                            if (!webSocketManager.isConnected()) {
                                Log.d("PostManageAdp", "WebSocket not connected, attempting to reconnect...");
                                webSocketManager.reconnect();
                            }
                            webSocketManager.sendMessage("offendPost:"+post_id);

                            post.setIs_offending(1);  // 更新状态
                            notifyDataSetChanged(); // 刷新列表
                        })
                        .setNegativeButton("取消", null)
                        .show();
            } else {

                Toast.makeText(context, "该帖子处于封禁中", Toast.LENGTH_SHORT).show();
            }
        });

        // Unblock button behavior
        viewHolder.unblock.setOnClickListener(v -> {
            if (is_blocked == 0) {
                // 提示用户该用户已经处于正常状态
                Toast.makeText(context, "该帖子处于正常状态", Toast.LENGTH_SHORT).show();
            } else {
                // 显示解封对话框
                new AlertDialog.Builder(context)
                        .setMessage("确定解封该帖子？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 发送解封请求到后端
                            webSocketManager = WebSocketManager.getInstance();
                            webSocketManager.logConnectionStatus();  // 记录连接状态

                            // 确保WebSocket已连接后再发送请求
                            if (!webSocketManager.isConnected()) {
                                Log.d("PostManageAdp", "WebSocket not connected, attempting to reconnect...");
                                webSocketManager.reconnect();
                            }
                            webSocketManager.sendMessage("unoffendPost:"+post_id);
                            post.setIs_offending(0);  // 更新用户状态
                            notifyDataSetChanged(); // 刷新列表
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        return convertView;
    }



    public static class ViewHolder {
        TextView postIdTextView;
        TextView postTitleTextView;
        TextView isBlockedTextView;
        Button block;
        Button unblock;

    }
}
