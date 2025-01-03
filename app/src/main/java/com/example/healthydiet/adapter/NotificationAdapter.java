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
import com.example.healthydiet.entity.Notification;
import com.example.healthydiet.entity.User;
import com.example.healthydiet.websocket.WebSocketManager;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private List<Notification> notificationList;
    private Context context;
    private WebSocketManager webSocketManager;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
            viewHolder = new NotificationAdapter.ViewHolder();
            viewHolder.timeTextView = convertView.findViewById(R.id.timeTextView);
            viewHolder.contentTextView = convertView.findViewById(R.id.contentTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NotificationAdapter.ViewHolder) convertView.getTag();
        }

        Notification notification = notificationList.get(position);

        viewHolder.timeTextView.setText(notification.getCreate_time());
        viewHolder.contentTextView.setText(notification.getData());


        return convertView;
    }



    public static class ViewHolder {
        TextView timeTextView;
        TextView contentTextView;

    }

}
