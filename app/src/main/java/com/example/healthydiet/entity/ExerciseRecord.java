package com.example.healthydiet.entity;

public class ExerciseRecord {
    private String exerciseName;
    private String date;
    private String duration;
    private int burnedCaloris;
    private int exerciseRecordId;

    // 完整构造函数
    public ExerciseRecord(int exerciseRecordId,String exerciseName, String date, String duration, int burnedCaloris) {
        this.exerciseRecordId=exerciseRecordId;
        this.exerciseName = exerciseName;
        this.date = date;
        this.duration = duration;
        this.burnedCaloris = burnedCaloris;
    }

    // Getter 和 Setter 方法
    public int getexerciseRecordId() {
        return exerciseRecordId;
    }

    public void setexerciseRecordId(int exerciseRecordId) {
        this.exerciseRecordId = exerciseRecordId;
    }
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
