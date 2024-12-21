package com.example.healthydiet.entity;

public class FoodItem {
    private String name;
    private String type;
    private int calories;
    private double carbohydrates;
    private double dietaryFiber;
    private double potassium;
    private double sodium;
    private int foodid;
    public FoodItem(String name, String type, int calories, double carbohydrates, double dietaryFiber, double potassium, double sodium) {
        this.name = name;
        this.type = type;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.dietaryFiber = dietaryFiber;
        this.potassium = potassium;
        this.sodium = sodium;
    }

    // Getter 和 Setter 方法

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCalories() {
        return calories;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getDietaryFiber() {
        return dietaryFiber;
    }

    public double getPotassium() {
        return potassium;
    }

    public double getSodium() {
        return sodium;
    }

    public void setFoodid(int foodid) {
        this.foodid = foodid;
    }

    public int getFoodid() {
        return foodid;
    }
}
