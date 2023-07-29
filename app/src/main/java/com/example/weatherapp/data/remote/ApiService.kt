package com.example.weatherapp.data.remote


import com.example.weatherapp.data.remote.entity.CurrentWeatherResponse
import com.example.weatherapp.data.remote.entity.ForecastWeatherResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    private val appId: String
        get() = "d32648bf5c3c34cecb0432b43ceb84f0"

    @GET("weather?")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = appId
    ): Response<CurrentWeatherResponse>

    @GET("forecast?")
    suspend fun getForecastWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = appId
    ): Response<ForecastWeatherResponse>

    @GET("weather?")
    suspend fun getCurrentbyLocation(
        @Query("q") loc: String,
        @Query("appid") appid: String = appId
    ): Response<CurrentWeatherResponse>
}