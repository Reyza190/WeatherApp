package com.example.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.entity.Forecast
import com.example.weatherapp.data.local.entity.Location
import com.example.weatherapp.data.remote.entity.CurrentWeatherResponse
import com.example.weatherapp.data.remote.entity.ForecastWeatherResponse
import kotlinx.coroutines.launch

class MainViewModel(private val weatherRepository: WeatherRepository): ViewModel() {

    private val _data = MutableLiveData<CurrentWeatherResponse?>()
    val data : LiveData<CurrentWeatherResponse?> = _data

    private val _userData = MutableLiveData<CurrentWeatherResponse?>()
    val userData : LiveData<CurrentWeatherResponse?> = _userData

    private val _weatherLocation = MutableLiveData<ForecastWeatherResponse?>()
    val weatherLocation : LiveData<ForecastWeatherResponse?> = _weatherLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message



    fun getCurrentbyLocation(q: String){
        viewModelScope.launch {
            when(val response = weatherRepository.getCurrentbyLocation(q)){
                is Result.Error -> {
                    _message.value = response.error
                    _isLoading.value = false
                }
                Result.Loading -> _isLoading.value = true
                is Result.Success -> {
                    _isLoading.value = false
                    _data.value = response.data
                }
            }
        }
    }

    fun getCurrentWeather(lat: String, lon: String){
        viewModelScope.launch {
            when(val response = weatherRepository.getCurrentWeather(lat, lon)){
                is Result.Error -> {
                    _message.value = response.error
                    _isLoading.value = false
                }
                Result.Loading -> _isLoading.value = true
                is Result.Success -> {
                    _isLoading.value = false
                    _userData.value = response.data
                }
            }
        }
    }



    fun getForecastWeather(lat: String, lon: String){
        viewModelScope.launch {
            when(val response = weatherRepository.getForecastWeather(lat, lon)){
                is Result.Error -> {
                    _message.value = response.error
                    _isLoading.value = false
                }
                Result.Loading -> _isLoading.value = true
                is Result.Success -> {
                    _isLoading.value = false
                    _weatherLocation.value = response.data

                }
            }
        }
    }

    suspend fun insertForecast(forecast: List<Forecast>){
        weatherRepository.insertForecast(forecast = forecast)
    }
    fun insertWeather(weather: Location){
        weatherRepository.insertWeather(weather)
    }
    suspend fun deleteAll(){
        weatherRepository.deleteAll()

    }

    suspend fun deleteAllWeather(){
        weatherRepository.deleteAllWeather()
    }

    fun getForecast() = weatherRepository.getForecast()

    fun getWeather() = weatherRepository.getWeather()

}