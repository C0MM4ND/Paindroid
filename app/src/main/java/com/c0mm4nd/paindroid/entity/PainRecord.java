package com.c0mm4nd.paindroid.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PainRecord {
    @PrimaryKey(autoGenerate = true)
    public int pid;

    @ColumnInfo
    @NonNull
    public String email;

    @ColumnInfo(name = "pain_date")
    public int painDate;

    @ColumnInfo(name = "pain_level")
    public int painLevel;

    @ColumnInfo(name = "pain_loc")
    @NonNull
    public String painLoc;

    @ColumnInfo(name = "mood_level")
    public int moodLevel;

    @ColumnInfo(name = "goal_steps")
    public int goalSteps;

    @ColumnInfo(name = "physical_steps")
    public int physicalSteps;


    @ColumnInfo
    public double temperature;

    @ColumnInfo
    public int humidity;

    @ColumnInfo
    public int pressure;

    public PainRecord(@NonNull String email, int painDate, int painLevel, @NonNull String painLoc,
                      int moodLevel, int goalSteps, int physicalSteps, double temperature, int humidity, int pressure) {
        this.email = email;
        this.painDate = painDate;
        this.painLevel = painLevel;
        this.painLoc = painLoc;
        this.moodLevel = moodLevel;
        this.physicalSteps = physicalSteps;
        this.goalSteps = goalSteps;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }
}
