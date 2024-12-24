package com.example.healthydiet.entity;

public class ExerciseRecord {
    private String exerciseName;
    private String date;
    private String duration;
    private int burnedCaloris;

    // 完整构造函数
    public ExerciseRecord(String exerciseName, String date, String duration, int burnedCaloris) {
        this.exerciseName = exerciseName;
        this.date = date;
        this.duration = duration;
        this.burnedCaloris = burnedCaloris;
    }

    // Getter 和 Setter 方法
    public String getexerciseName() {
        return exerciseName;
    }

    public void setexerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getBurnedCaloris() {
        return burnedCaloris;
    }

    public void setBurnedCaloris(int burnedCaloris) {
        this.burnedCaloris = burnedCaloris;
    }
}
