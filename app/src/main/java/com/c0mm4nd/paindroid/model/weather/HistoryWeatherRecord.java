package com.c0mm4nd.paindroid.model.weather;

import com.google.gson.annotations.SerializedName;

import java.security.Timestamp;

public class HistoryWeatherRecord {
    @SerializedName("dt")
    public Timestamp dt;

    @SerializedName("main")
    public Weather main;

    public Timestamp getDt() {
        return dt;
    }

    public void setDt(Timestamp dt) {
        this.dt = dt;
    }

    public Weather getMain() {
        return main;
    }

    public void setMain(Weather main) {
        this.main = main;
    }
}
