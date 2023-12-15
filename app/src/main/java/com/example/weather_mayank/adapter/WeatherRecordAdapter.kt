package com.example.weather_mayank.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.weather_mayank.data.WeatherReport
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_mayank.databinding.WeatherRecordBinding

class WeatherRecordAdapter(private val context:Context, var reportList:MutableList<WeatherReport>):
    RecyclerView.Adapter<WeatherRecordAdapter.WeatherRecordHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherRecordHolder {
        return WeatherRecordHolder(WeatherRecordBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: WeatherRecordAdapter.WeatherRecordHolder, position: Int) {
        val currentItem: WeatherReport = reportList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return reportList.size
    }

    inner class WeatherRecordHolder(b: WeatherRecordBinding):RecyclerView.ViewHolder(b.getRoot()) {
        var binding : WeatherRecordBinding

        fun bind(currentItem : WeatherReport)
        {
            var temp:Double = String.format("%.2f",currentItem.temp).toDouble()
            if (currentItem != null){
                binding.city.text = currentItem.city?.toUpperCase()
                binding.date.text = "DATE:${currentItem.date}"
                binding.time.text = "TIME:${currentItem.time}"
                binding.descBlock.text = "Temperature: ${currentItem.temp} C\n" +
                        "Conditions: ${currentItem.conditions}\n" +
                        "Description: ${currentItem.description}"
            }


        }
        init {
            binding = b
        }


    }

}