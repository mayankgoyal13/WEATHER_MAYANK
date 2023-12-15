package com.example.weather_mayank.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "table_weather")
class WeatherReport(
    val date:String,
    val time: String,
    val city: String?,
    val temp: Double,
    val conditions: String,
    val description: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    override fun toString(): String {
        return "WeatherReport(date='$date', time='$time', city=$city, temp=$temp, conditions='$conditions', description='$description', id=$id)"
    }


}