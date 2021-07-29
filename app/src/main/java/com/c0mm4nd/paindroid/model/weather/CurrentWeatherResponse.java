package com.c0mm4nd.paindroid.model.weather;

import com.google.gson.annotations.SerializedName;

// {
//  "coord":{
//    "lon":140.5667,
//    "lat":-34.2333
//  },
//  "weather":[
//    {
//      "id":800,
//      "main":"Clear",
//      "description":"clear sky",
//      "icon":"01n"
//    }
//  ],
//  "base":"stations",
//  "main":{
//    "temp":287.18,
//    "feels_like":286.21,
//    "temp_min":287.18,
//    "temp_max":287.18,
//    "pressure":1020,
//    "humidity":60,
//    "sea_level":1020,
//    "grnd_level":1017
//  },
//  "visibility":10000,
//  "wind":{
//    "speed":2.33,
//    "deg":259,
//    "gust":2.38
//  },
//  "clouds":{
//    "all":0
//  },
//  "dt":1620810756,
//  "sys":{
//    "country":"AU",
//    "sunrise":1620768071,
//    "sunset":1620805626
//  },
//  "timezone":34200,
//  "id":2157247,
//  "name":"Monash",
//  "cod":200
//}
public class CurrentWeatherResponse {
    @SerializedName("main")
    public Weather main;

    public Weather getMain() {
        return main;
    }

    public void setMain(Weather main) {
        this.main = main;
    }
}
