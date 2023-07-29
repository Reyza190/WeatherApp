package com.example.weatherapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Utils
import com.example.weatherapp.data.local.entity.Location
import com.example.weatherapp.databinding.WeatherCardBinding

class WeatherAdapter : ListAdapter<Location, WeatherAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: WeatherCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(location: Location){
            binding.ivWeatherLoc.setImageResource(Utils.weatherType(location.icon))
            binding.tvLocationLoc.text = location.location
            binding.tvTemperatureLoc.text = location.temp + "℃"
            binding.tvCondition.text = location.condition
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Location>() {
            override fun areItemsTheSame(
                oldItem: Location,
                newItem: Location
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Location,
                newItem: Location
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            WeatherCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val weather = getItem(position)
        holder.bind(weather)
    }
}