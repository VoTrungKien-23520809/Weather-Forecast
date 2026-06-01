package com.kienvo.jetweatherforecast.di

import android.content.Context
import androidx.room.Room
import com.kienvo.jetweatherforecast.data.local.FavoriteDao
import com.kienvo.jetweatherforecast.data.local.SearchHistoryDao
import com.kienvo.jetweatherforecast.data.local.UnitDao
import com.kienvo.jetweatherforecast.data.local.WeatherDatabase
import com.kienvo.jetweatherforecast.data.network.WeatherApi
import com.kienvo.jetweatherforecast.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson để parse JSON
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database" // Tên file lưu trữ dưới máy người dùng
        )
            .fallbackToDestructiveMigration() // Tự động xóa data cũ nếu sau này bạn thay đổi cấu trúc bảng
            .build()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: WeatherDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: WeatherDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideUnitDao(database: WeatherDatabase): UnitDao {
        return database.unitDao()
    }
}