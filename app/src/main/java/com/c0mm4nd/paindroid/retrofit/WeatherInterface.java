package com.c0mm4nd.paindroid.retrofit;

import com.c0mm4nd.paindroid.model.weather.CurrentWeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherInterface {

    @GET("/data/2.5/weather")
    Call<CurrentWeatherResponse> fetchCurrentWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String appID
    );

    @GET("/data/2.5/history/city")
    Call<CurrentWeatherResponse> fetchHourlyHistoryWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("type") String type,
            @Query("start") Integer start,
            @Query("end") Integer end,
            @Query("appid") String appID
    );
}
