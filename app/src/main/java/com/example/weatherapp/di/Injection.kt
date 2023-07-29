package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.AppExecutors
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.database.WeatherDatabase
import com.example.weatherapp.data.remote.ApiConfig

object Injection {
    fun provideRepository(context: Context): WeatherRepository {
        val apiService = ApiConfig.getApiService()
        val database = WeatherDatabase.getInstance(context)
        val appExecutors = AppExecutors()
        return WeatherRepository(apiService, database, appExecutors)
    }
}