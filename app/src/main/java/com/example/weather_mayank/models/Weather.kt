package com.example.weather_mayank.models


data class Weather(
    val queryCost: Long,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Double,
    val description: String,
    val days: List<Day>,
    val currentConditions: CurrentConditions,
)