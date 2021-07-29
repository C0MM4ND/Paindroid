package com.c0mm4nd.paindroid.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClient {
    private static final String CURRENT_WEATHER_BASE_URL = "https://api.openweathermap.org";
    private static final String HISTORY_WEATHER_BASE_URL = "https://history.openweathermap.org";
    private static Retrofit retrofit;

    public static WeatherInterface getCurrentWeatherService() {
        retrofit = new Retrofit.Builder().baseUrl(CURRENT_WEATHER_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(WeatherInterface.class);
    }

    public static WeatherInterface getHistoryWeatherService() {
        retrofit = new Retrofit.Builder().baseUrl(HISTORY_WEATHER_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(WeatherInterface.class);
    }
}
