package com.example.weatherapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.data.remote.entity.WeatherItem

@Entity(tableName = "forecast")
data class Forecast(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "temperature")
    val temperatur: Double
)
