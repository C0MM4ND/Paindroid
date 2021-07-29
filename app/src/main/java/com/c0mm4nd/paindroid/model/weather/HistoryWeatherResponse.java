package com.c0mm4nd.paindroid.model.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryWeatherResponse {
    @SerializedName("list")
    public List<HistoryWeatherRecord> list;

    public List<HistoryWeatherRecord> getList() {
        return list;
    }

    public void setList(List<HistoryWeatherRecord> list) {
        this.list = list;
    }
}
