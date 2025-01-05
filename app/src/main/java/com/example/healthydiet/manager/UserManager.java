package com.example.healthydiet.manager;

import com.example.healthydiet.entity.User;

public class UserManager {
    private static UserManager instance;
    private User user;

    // 私有构造函数，防止外部创建多个实例
    private UserManager() {}

    // 获取唯一实例的方法
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // 设置 User
    public void setUser(User user) {
        this.user = user;
    }

    // 获取 User
    public User getUser() {
        return user;
    }
}
