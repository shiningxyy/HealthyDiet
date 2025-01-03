package com.example.healthydiet.entity;

public class Notification {
    private int notification_id;
    private String data;
    private int user_id;
    private String create_time;
    public Notification(int notification_id,String data,int user_id,String create_time){
        this.create_time=create_time;
        this.data=data;
        this.notification_id=notification_id;
        this.user_id=user_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getData() {
        return data;
    }
}
