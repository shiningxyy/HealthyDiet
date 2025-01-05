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
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;

import java.util.List;

public class UserManageAdapter extends BaseAdapter {
    private List<User> userList;
    private Context context;
    private WebSocketManager webSocketManager;

    public UserManageAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserManageAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_manage, parent, false);
            viewHolder = new UserManageAdapter.ViewHolder();
            viewHolder.userIdTextView = convertView.findViewById(R.id.userIdTextView);
            viewHolder.isBlockedTextView = convertView.findViewById(R.id.isBlockedTextView);
            viewHolder.block = convertView.findViewById(R.id.block);
            viewHolder.unblock = convertView.findViewById(R.id.unblock);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UserManageAdapter.ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);
        int user_id=user.getUserId();
        int is_blocked=user.getIsblocked();

        viewHolder.userIdTextView.setText("用户ID:"+user_id);
        if(is_blocked==0){
            viewHolder.isBlockedTextView.setText(" 封禁状态：正常");
        }
        else{
            viewHolder.isBlockedTextView.setText(" 封禁状态：封禁中");
        }

        // Block button behavior
        viewHolder.block.setOnClickListener(v -> {
            if (is_blocked == 0) {
                // 显示封禁对话框
                new AlertDialog.Builder(context)
                        .setMessage("确定封禁该用户？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 发送封禁请求到后端
                            webSocketManager = WebSocketManager.getInstance();
                            webSocketManager.logConnectionStatus();  // 记录连接状态

                            // 确保WebSocket已连接后再发送请求
                            if (!webSocketManager.isConnected()) {
                                Log.d("UserAdapter", "WebSocket not connected, attempting to reconnect...");
                                webSocketManager.reconnect();
                            }
                            webSocketManager.sendMessage("blockUser:"+user_id);

                            user.setIsblocked(1);  // 更新用户状态
                            notifyDataSetChanged(); // 刷新列表
                        })
                        .setNegativeButton("取消", null)
                        .show();
            } else {
                // 提示用户该用户已经被封禁
                Toast.makeText(context, "该用户处于封禁中", Toast.LENGTH_SHORT).show();
            }
        });

        // Unblock button behavior
        viewHolder.unblock.setOnClickListener(v -> {
            if (is_blocked == 0) {
                // 提示用户该用户已经处于正常状态
                Toast.makeText(context, "该用户处于正常状态", Toast.LENGTH_SHORT).show();
            } else {
                // 显示解封对话框
                new AlertDialog.Builder(context)
                        .setMessage("确定解封该用户？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 发送解封请求到后端
                            webSocketManager = WebSocketManager.getInstance();
                            webSocketManager.logConnectionStatus();  // 记录连接状态

                            // 确保WebSocket已连接后再发送请求
                            if (!webSocketManager.isConnected()) {
                                Log.d("UserAdapter", "WebSocket not connected, attempting to reconnect...");
                                webSocketManager.reconnect();
                            }
                            webSocketManager.sendMessage("unblockUser:"+user_id);
                            user.setIsblocked(0);  // 更新用户状态
                            notifyDataSetChanged(); // 刷新列表
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        return convertView;
    }



    public static class ViewHolder {
        TextView userIdTextView;
        TextView isBlockedTextView;
        Button block;
        Button unblock;
    }
}
