package com.example.healthydiet.entity;

import java.io.Serializable;

public class User {
    private String phone;
    private String password;
    private String name;
    private int age;
    private int height;
    private double weight;
    private int userId;
    private String profilePicture;
    private int isblocked;
    private int gender;
    private double activity_factor;
    // 构造函数、getter 和 setter
    public User(String name,String password,int weight, int age, int height,String phone,int gender,double activity_factor) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender=gender;
        this.activity_factor=activity_factor;
    }

    // Getter 和 Setter 方法
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public int getIsblocked() {
        return isblocked;
    }

    public void setIsblocked(int isblocked) {
        this.isblocked = isblocked;
    }

    public double getActivity_factor() {
        return activity_factor;
    }

    public void setActivity_factor(double activity_factor) {
        this.activity_factor = activity_factor;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
