package com.example.weatherapp.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.local.entity.Location

@Dao
interface WeatherDao {
    @Query("SELECT * FROM locationWeather")
    fun getWeather(): LiveData<List<Location>>

    @Query("SELECT * FROM locationWeather where locationWeather.id = :id")
    fun getOneWeather(id: Int): LiveData<Location>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: Location)

    @Query("DELETE FROM locationWeather")
    suspend fun deleteAll()
}