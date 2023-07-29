package com.example.weatherapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherapp.AppExecutors
import com.example.weatherapp.data.local.database.WeatherDatabase
import com.example.weatherapp.data.local.entity.Forecast
import com.example.weatherapp.data.local.entity.Location
import com.example.weatherapp.data.remote.ApiService
import com.example.weatherapp.data.remote.entity.CurrentWeatherResponse
import com.example.weatherapp.data.remote.entity.ForecastWeatherResponse

class WeatherRepository(
    private val apiService: ApiService,
    private val weatherDatabase: WeatherDatabase,
    private val appExecutors: AppExecutors
) {
    suspend fun getCurrentWeather(
        lat: String,
        lon: String
    ): Result<CurrentWeatherResponse> {
        weatherDatabase.weatherDao().deleteAll()
        return try {
            val response = apiService.getCurrentWeather(lat, lon)
            if (response.isSuccessful) {
                val result = response.body()
                Result.Success(result!!)
            } else {
                val error = response.errorBody().toString()
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Main: ${e.message.toString()}")
            Result.Error(e.toString())
        }
    }

    suspend fun getCurrentbyLocation(
        q: String
    ): Result<CurrentWeatherResponse> {
        return try {
            val response = apiService.getCurrentbyLocation(q)
            if (response.isSuccessful) {
                val result = response.body()
                Result.Success(result!!)
            } else {
                val error = response.errorBody().toString()
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Main: ${e.message.toString()}")
            Result.Error(e.toString())
        }
    }

    suspend fun getForecastWeather(
        lat: String,
        lon: String
    ): Result<ForecastWeatherResponse> {
        weatherDatabase.forecastDao().deleteAll()
        return try {
            val response = apiService.getForecastWeather(lat, lon)
            if (response.isSuccessful) {
                val result = response.body()
                Result.Success(result!!)
            } else {
                val error = response.errorBody().toString()
                Result.Error(error)
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Main: ${e.message.toString()}")
            Result.Error(e.toString())
        }
    }

    suspend fun insertForecast(forecast: List<Forecast>) {

        weatherDatabase.forecastDao().insertForecast(forecast)

    }

    suspend fun deleteAll() {
        weatherDatabase.forecastDao().deleteAll()

    }

    suspend fun deleteAllWeather() {
        weatherDatabase.weatherDao().deleteAll()
    }

    fun getForecast(): LiveData<List<Forecast>> {
        return weatherDatabase.forecastDao().getForecast()
    }

    fun getWeather(): LiveData<List<Location>> {
        return weatherDatabase.weatherDao().getWeather()
    }

    fun getOneWeather(id: Int): LiveData<Location> {
        return weatherDatabase.weatherDao().getOneWeather(id)
    }

    fun insertWeather(weather: Location) {
        appExecutors.diskIO.execute {
            weatherDatabase.weatherDao().insertWeather(weather)
        }
    }

}