package com.example.weather_mayank.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

@Database(entities = [WeatherReport::class], version = 1 , exportSchema = false)
abstract class AppDB: RoomDatabase(){
    abstract fun weatherDAO(): WeatherDAO

    companion object{
        private var dbInstance: AppDB? = null
        private const val NUMBER_OF_THREADS = 3
        val databaseQueryExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDB(context: Context):AppDB?{
            if (dbInstance == null){
                dbInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    name = "com.example.weather_mayank"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return dbInstance
        }
    }
}