package com.example.weather_mayank.api

import com.example.weather_mayank.models.Weather
import retrofit2.http.GET
import retrofit2.http.Path


interface WeatherAppInterface {


    @GET("/VisualCrossingWebServices/rest/services/timeline/{latitude},{longitude}?unitGroup=metric&key=PK3LTA2K8SAN2KEZCUXD2VHFG&contentType=json")
    suspend fun getWeatherByCoordinates(
        @Path("latitude")latitude:Double,
        @Path("longitude")longitude: Double
    ): Weather

}


