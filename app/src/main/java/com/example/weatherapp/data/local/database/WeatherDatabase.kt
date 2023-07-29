package com.example.weatherapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weatherapp.data.local.entity.Forecast
import com.example.weatherapp.data.local.entity.Location

@Database(entities = [Location::class, Forecast::class], version = 2, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java, "Weather.db"
                ) .addMigrations(MIGRATION_1_2) // Tambahkan migrasi ke versi 2
                    .build()
                    .also { INSTANCE = it }
            }
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE locationWeather ADD COLUMN date TEXT")
            }
        }
    }

}