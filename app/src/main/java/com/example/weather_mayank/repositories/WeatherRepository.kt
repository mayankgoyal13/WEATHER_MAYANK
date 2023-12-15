package com.example.weather_mayank.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.weather_mayank.data.AppDB
import com.example.weather_mayank.data.WeatherReport

class WeatherRepository(application: Application) {
    private var dbInstance: AppDB? = null
    private val weatherDAO = AppDB.getDB(application)?.weatherDAO()

    var retrievedWeatherData: LiveData<List<WeatherReport>>? = weatherDAO?.fetchWeatherReport()

    init {
        this.dbInstance = AppDB.getDB(application)
    }

    fun saveReport(report: WeatherReport){
        AppDB.databaseQueryExecutor.execute{
            this.weatherDAO?.saveWeatherReport(report)
        }
    }

}