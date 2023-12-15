package com.example.weather_mayank.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface WeatherDAO {

    @Insert
    fun saveWeatherReport(report:WeatherReport)

    @Query("SELECT * FROM TABLE_WEATHER ORDER BY date DESC , time DESC")
    fun fetchWeatherReport():LiveData<List<WeatherReport>>


}