package com.example.healthydiet.entity;

import java.io.Serializable;

public class FoodRecord  implements Serializable {
    private int foodRecordId;
    private String recordTime;        // 记录时间（例如：2024-12-24T12:34:56）
    private int userId;            // 用户ID
    private int foodId;               // 食物ID
    private double foodWeight;           // 食物重量（单位：克）
    private int calories;             // 卡路里
    private double fat;               // 脂肪含量
    private double protein;           // 蛋白质含量
    private double carbohydrates;     // 碳水化合物含量
    private double sodium;            // 钠含量
    private double potassium;         // 钾含量
    private double dietaryFiber;      // 膳食纤维含量
    private String foodName;  // 添加 foodName 字段
    // 无参构造方法
    public FoodRecord() {}

    // 带参构造方法
    public FoodRecord(int foodRecordId,String foodName, String recordTime, int userId, int foodId, double foodWeight,
                      int calories, double fat, double protein, double carbohydrates,
                      double sodium, double potassium, double dietaryFiber) {
        this.foodRecordId=foodRecordId;
        this.recordTime = recordTime;
        this.userId = userId;
        this.foodId = foodId;
        this.foodWeight = foodWeight;
        this.calories = calories;
        this.fat = fat;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.sodium = sodium;
        this.potassium = potassium;
        this.dietaryFiber = dietaryFiber;
        this.foodName=foodName;
    }
    public int getFoodRecordId() {
        return foodRecordId;
    }

    public void setFoodRecordId(String foodName) {
        this.foodRecordId = foodRecordId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    // Getter 和 Setter 方法
    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public double getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(double foodWeight) {
        this.foodWeight = foodWeight;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(double dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

}
