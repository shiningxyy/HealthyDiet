package com.example.healthydiet.entity;

public class FoodItem {
    private String name;
    private String type;
    private int calories;
    private double carbohydrates;
    private double dietaryFiber;
    private double potassium;
    private double sodium;
    private double fat;
    private double protein;
    private int foodid;
    public FoodItem(String name, String type, int calories, double carbohydrates, double dietaryFiber, double potassium, double sodium,
                    double fat,double protein) {
        this.name = name;
        this.type = type;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.dietaryFiber = dietaryFiber;
        this.potassium = potassium;
        this.sodium = sodium;
        this.fat=fat;
        this.protein=protein;
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
}
