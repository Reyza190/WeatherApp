package com.example.weatherapp

import android.content.ContentValues
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.Result
import com.example.weatherapp.data.local.entity.Location
import com.example.weatherapp.data.remote.entity.CurrentWeatherResponse
import com.google.gson.Gson
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Random

object Utils {
    fun weatherType(icon: String): Int {
        return when(icon){
            "01d" -> R.drawable.oned
            "01n" -> R.drawable.onen
            "02d" -> R.drawable.twod
            "02n" -> R.drawable.twon
            "03d","03n" -> R.drawable.threedn
            "10d" -> R.drawable.tend
            "10n" -> R.drawable.tenn
            "04d", "04n" -> R.drawable.fourdn
            "09d", "09n" -> R.drawable.ninedn
            "11d", "11n" -> R.drawable.elevend
            "13d", "13n" -> R.drawable.thirteend
            "50d", "50n" -> R.drawable.fiftydn
            else -> {
                return 0
            }
        }
    }
    fun converKelvinToCelcius(kelvin: Double): Int{
        return (kelvin - 273.15).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayForecast(date: String): String{
        val currentDateTime = LocalDateTime.now()
        val currentDateO = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return if (date.split("\\s".toRegex()).contains(currentDateO)){
            date
        } else "null"
    }

    fun getAddressName(context: Context,lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].subLocality
                Log.d(ContentValues.TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mapper(location: String, result: CurrentWeatherResponse) : Location {
        val random = (2 until 10).random()

        return Location(
            id = random,
            location = location,
            icon = result.weather?.get(0)?.icon.toString(),
            condition = result.weather?.get(0)?.main.toString(),
            temp = converKelvinToCelcius(result.main!!.temp as Double).toString(),
            humidity = result.main.humidity.toString(),
            wind = result.wind!!.speed.toString(),
            date = getCurrentDateTime()
        )
    }
    val cities = listOf("New York", "Singapore", "Mumbai", "Delhi", "Sydney", "Melbourne")

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (connectivityManager != null) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Untuk API level 23 atau di atasnya (Android 6.0+), gunakan NetworkCapabilities
                val network = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            } else {
                // Untuk API level sebelumnya, gunakan deprecated methods
                val networkInfo = connectivityManager.activeNetworkInfo
                networkInfo != null && networkInfo.isConnected
            }
        }

        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }


    object SharedPreferencesUtil {

        private const val PREF_NAME = "my_preferences"
        private const val KEY_WEATHER_DATA = "weather_data"

        private val gson = Gson()

        fun saveWeatherData(context: Context, weatherData: Location) {
            val jsonString = gson.toJson(weatherData)
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_WEATHER_DATA, jsonString)
            editor.apply()
        }

        fun getWeatherData(context: Context): Location? {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val jsonString = sharedPreferences.getString(KEY_WEATHER_DATA, null)
            return if (jsonString != null) {
                gson.fromJson(jsonString, Location::class.java)
            } else {
                null
            }
        }
        fun removeWeatherData(context: Context) {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove(KEY_WEATHER_DATA)
            editor.apply()
        }
    }
}