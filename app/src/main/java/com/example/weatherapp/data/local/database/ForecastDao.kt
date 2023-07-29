package com.example.weatherapp.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.local.entity.Forecast
import com.example.weatherapp.data.local.entity.Location

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast")
    fun getForecast(): LiveData<List<Forecast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<Forecast>)

    @Query("DELETE FROM forecast")
    suspend fun deleteAll()
}