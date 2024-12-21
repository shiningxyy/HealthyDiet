package com.example.healthydiet.entity;

public class User {
    private String phone;
    private String password;
    private String name;
    private int age;
    private int height;
    private int weight;
    private int userId;
    private String profilePicture;
    private int isblocked;
    // 构造函数、getter 和 setter
    public User(String name,String password,int weight,  int age, int height,String phone) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;

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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
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
}
