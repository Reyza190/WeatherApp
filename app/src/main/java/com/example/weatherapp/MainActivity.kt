package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapter.ForecastAdapter
import com.example.weatherapp.adapter.WeatherAdapter
import com.example.weatherapp.data.local.entity.Forecast
import com.example.weatherapp.data.remote.entity.CurrentWeatherResponse
import com.example.weatherapp.data.remote.entity.ForecastWeatherResponse
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var size = 0
    private val adapter = WeatherAdapter()
    private val forecastAdapter = ForecastAdapter()
    private val locationWeather: ArrayList<com.example.weatherapp.data.local.entity.Location> =
        arrayListOf()
    private var addressName: String = ""
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }


    private fun checkLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions granted, get the current location
                getCurrentLocation()
            } else {
                // Permissions denied, handle accordingly
                // For example, show an error message or disable location-based features
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkLocationPermissions()) {
            // Permissions are granted, proceed to get the current location
            if (Utils.isInternetAvailable(this)) {
                Utils.SharedPreferencesUtil.removeWeatherData(this)
                getCurrentLocation()
            }
        } else {
            // Request location permissions
            requestLocationPermissions()
        }
        //Setup Loading
        loadingSetup()

        //init Weather RecycleView
        rvWeatherSetup()

        //init Forecast RecycleView
        rvForecastSetup()

        getData()

        //user card view update
        updateUserUi()

        swipeLayoutSetup()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun swipeLayoutSetup() {
        binding.swlData.setOnRefreshListener {
            if (Utils.isInternetAvailable(this)) {
                Utils.SharedPreferencesUtil.removeWeatherData(this)
                loadData()
            } else {
                val sharedPreferences = Utils.SharedPreferencesUtil.getWeatherData(this)
                val date = sharedPreferences?.date?.substring(11, 16)
                val announ = "You're Offline Latest Data Taken at $date"
                Toast.makeText(this, announ, Toast.LENGTH_SHORT).show()
                binding.swlData.isRefreshing = false
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        Utils.SharedPreferencesUtil.removeWeatherData(this)
        getCurrentLocation()
        getData()
        binding.swlData.isRefreshing = false
    }

    private fun loadingSetup() {
        mainViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun rvWeatherSetup() {
        val layoutManagerWeather = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeather.layoutManager = layoutManagerWeather
        binding.rvWeather.adapter = adapter
        setDataWeather()
    }

    private fun rvForecastSetup() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvForecast.layoutManager = layoutManager
        mainViewModel.getForecast().observe(this) { forecast ->
            forecastAdapter.submitList(forecast)
            size = forecast.size
            binding.rvForecast.adapter = forecastAdapter
        }
    }


    //GET THE LOCATION AND CALL THE API
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location: Location? =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                addressName = Utils.getAddressName(this, latitude, longitude)!!
                Utils.SharedPreferencesUtil.removeWeatherData(this)
                mainViewModel.getCurrentWeather(latitude.toString(), longitude.toString())
                mainViewModel.userData.observe(this) {
                    updateUserLocationUi(it)
                }
                mainViewModel.getForecastWeather(latitude.toString(), longitude.toString())
                mainViewModel.weatherLocation.observe(this) {
                    setDataForecast(it!!)
                }
                getData()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataWeather() {
        mainViewModel.getWeather().observe(this) {
            adapter.submitList(it)
            Log.e("ada", it.toString())
        }
    }

    //Update User Weather Info
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun updateUserLocationUi(it: CurrentWeatherResponse?) {
        Utils.SharedPreferencesUtil.removeWeatherData(this)
        val data = Utils.mapper(it?.name.toString(), it!!)
        Utils.SharedPreferencesUtil.saveWeatherData(this, data)
        updateUserUi()

    }

    private fun updateUserUi() {
        val getData = Utils.SharedPreferencesUtil.getWeatherData(this)
        Log.e("adas", getData.toString())
        val image = Utils.weatherType(getData?.icon.toString())
        binding.ivWeather.setImageResource(image)
        binding.tvLocation.text = getData?.location
        binding.tvTemperature.text = "${getData?.temp}℃"
        binding.tvTitle.text = getData?.condition
        binding.tvHumidity.text = getData?.humidity.toString()
        binding.tvWind.text = "${getData?.wind}m/s"
    }


    //SET DATA FORECAST PER 3 HOUR
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataForecast(result: ForecastWeatherResponse) {
        lifecycleScope.launch {
            mainViewModel.deleteAll()
        }
        val uniqueDatesSet = HashSet<String>()
        val arrayList: ArrayList<Forecast> = arrayListOf()
        result.list!!.forEach {
            val data = Utils.getTodayForecast(it!!.dtTxt.toString())
            if (data != "null") {
                it.weather!!.forEach { forecastWeather ->
                    val forecast = Forecast(
                        0,
                        forecastWeather!!.icon.toString(),
                        forecastWeather.description.toString(),
                        it.dtTxt.toString(),
                        it.main!!.temp as Double
                    )
                    if (uniqueDatesSet.add(it.dtTxt.toString())) {
                        arrayList.add(forecast)
                    }

                }
            }
        }
        lifecycleScope.launch {
            mainViewModel.insertForecast(arrayList)
        }
    }

    //Set Data  6 Location weather
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getData() {
        val uniqueDatesSet = HashSet<String>()
        Utils.cities.forEach {
            mainViewModel.getCurrentbyLocation(it)
        }
        mainViewModel.data.observe(this) { current ->
            val data = Utils.mapper(current?.name.toString(), current!!)
            if (uniqueDatesSet.add(data.location)){
                locationWeather.add(data)
                Log.e("data", locationWeather.toString())
                mainViewModel.insertWeather(data)
            }

        }
    }

}




