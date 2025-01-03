package com.example.healthydiet.entity;

import java.util.Objects;

public class WeightRecord {
    private String time;   // 记录的时间
    private double weight; // 体重
    private int userId;    // 用户ID

    // 无参构造函数
    public WeightRecord() {
    }

    // 带参构造函数
    public WeightRecord(int userId, double weight, String time) {
        this.time = time;
        this.weight = weight;
        this.userId = userId;
    }

    // Getter 和 Setter 方法
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


}
