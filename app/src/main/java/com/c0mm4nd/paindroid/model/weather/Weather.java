package com.c0mm4nd.paindroid.model.weather;


import com.google.gson.annotations.SerializedName;

//  {
//    "temp":287.18,
//    "feels_like":286.21,
//    "temp_min":287.18,
//    "temp_max":287.18,
//    "pressure":1020,
//    "humidity":60,
//    "sea_level":1020,
//    "grnd_level":1017
//  },

public class Weather {
    @SerializedName("temp")
    public double temperature;

    @SerializedName("humidity")
    public Integer humidity;

    @SerializedName("pressure")
    public Integer pressure;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }
}
