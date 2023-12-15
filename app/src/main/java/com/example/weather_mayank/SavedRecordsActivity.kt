package com.example.weather_mayank

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_mayank.adapter.WeatherRecordAdapter
import com.example.weather_mayank.data.WeatherReport
import com.example.weather_mayank.databinding.ActivitySavedRecordsBinding
import com.example.weather_mayank.repositories.WeatherRepository

class SavedRecordsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedRecordsBinding
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherAdapter:WeatherRecordAdapter
    private  var savedRecordList:MutableList<WeatherReport> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivitySavedRecordsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        setSupportActionBar(this.binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        setTitle("Saved Records")
        this.weatherRepository = WeatherRepository(application)


        weatherAdapter = WeatherRecordAdapter(applicationContext, savedRecordList)
        binding!!.rv.setAdapter(weatherAdapter)
        binding!!.rv.layoutManager = LinearLayoutManager(this)
        binding!!.rv.addItemDecoration(
            DividerItemDecoration(
                this,LinearLayoutManager.VERTICAL
            )
        )

    }


    override fun onStart() {
        super.onStart()
        this.weatherRepository.retrievedWeatherData?.observe(this){
            record ->
            if(record.isNotEmpty()){
                savedRecordList.clear()
                savedRecordList.addAll(record)
                weatherAdapter.notifyDataSetChanged()
            }else{
                Log.d("Data retrieving", "onStart: No record found ")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.homePage -> {
                finish()
                return true
            }

            R.id.savedReports -> {
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}