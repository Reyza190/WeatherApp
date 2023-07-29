package com.example.weatherapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.data.remote.entity.WeatherItem

@Entity(tableName = "locationWeather")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int ,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "temp")
    val temp: String,

    @ColumnInfo(name = "humidity")
    val humidity: String,

    @ColumnInfo(name = "wind")
    val wind: String,

    @ColumnInfo(name = "condition")
    val condition: String,

    @ColumnInfo(name = "date")
    val date: String
)
