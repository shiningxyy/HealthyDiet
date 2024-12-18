package com.example.healthydiet.entity;

public class User {
    private String phone;
    private String password;
    private String name;
    private int age;
    private int height;
    private int weight;
    private boolean login;  // 添加 login 字段
    // 构造函数、getter 和 setter
    public User(String name,String password,int weight,  int age, int height,String phone) {
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.login = true;  // 设置为 true，符合后端要求
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

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}
