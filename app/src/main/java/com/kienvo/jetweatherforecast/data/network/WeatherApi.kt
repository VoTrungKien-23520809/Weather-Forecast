package com.kienvo.jetweatherforecast.data.network

import com.kienvo.jetweatherforecast.BuildConfig
import com.kienvo.jetweatherforecast.data.model.CitySuggestion
import com.kienvo.jetweatherforecast.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Nối tiếp vào BASE_URL sẽ là endpoint này
    @GET("forecast/daily")
    suspend fun getWeather(
        @Query("q") query: String, // Tên thành phố (ví dụ: "Saigon", "Vinh Long")
        @Query("units") units: String = "metric", // Chuyển sang độ C
        @Query("appid") appid: String = BuildConfig.API_KEY // Lấy key bảo mật đã giấu
    ): WeatherResponse // Trả về file tổng mà chúng ta đã gom gọn ở bài 11

    @GET("https://api.openweathermap.org/geo/1.0/direct")
    suspend fun searchCity(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") appid: String = BuildConfig.API_KEY
    ): List<CitySuggestion>
}