package com.example.weatherapp.adapter


import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import  androidx.recyclerview.widget.ListAdapter

import com.example.weatherapp.databinding.ForecastCardBinding
import com.example.weatherapp.Utils
import com.example.weatherapp.data.local.entity.Forecast

class ForecastAdapter : ListAdapter<Forecast, ForecastAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {
    class MyViewHolder(private val binding: ForecastCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(forecast: Forecast) {
            binding.ivForecast.setImageResource(Utils.weatherType(forecast.icon))

            binding.tvTemperatureForecast.text =
                Utils.converKelvinToCelcius(forecast.temperatur).toString() + "℃"


            binding.tvTimeForecast.text = forecast.date.substring(11, 16)

        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding =
            ForecastCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Forecast>() {
            override fun areItemsTheSame(
                oldItem: Forecast,
                newItem: Forecast
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Forecast,
                newItem: Forecast
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}