package com.example.healthydiet.entity;

public class ExerciseRecord {
    private int exerciseRecordId;
    private int exerciseId;
    private int userId;
    private String date;
    private String duration;
    private int burnedCaloris;

    // 完整构造函数
    public ExerciseRecord(int exerciseId, String date, String duration, int burnedCaloris) {
        this.exerciseId = exerciseId;
        this.date = date;
        this.duration = duration;
        this.burnedCaloris = burnedCaloris;
    }

    // Getter 和 Setter 方法
    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getExerciseRecordId() {
        return exerciseRecordId;
    }

    public void setExerciseRecordId(int exerciseRecordId) {
        this.exerciseRecordId = exerciseRecordId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
