package com.example.healthydiet.entity;
public class ExerciseItem {
    private int exerciseId;
    private String name;
    private double caloriesPerHour;

    public ExerciseItem(String name, double caloriesPerHour) {
        this.name = name;
        this.caloriesPerHour = caloriesPerHour;

    }

    public int getExerciseId() {
        return exerciseId;
    }
    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public double getCaloriesPerHour() {
        return caloriesPerHour;
    }
    public void setCaloriesPerHour(double caloriesPerHour) {
        this.caloriesPerHour = caloriesPerHour;
    }
}
