package com.kienvo.jetweatherforecast.data.model

import com.google.gson.annotations.SerializedName

// Đây là class gốc chứa toàn bộ dữ liệu trả về từ API
data class WeatherResponse(
    val city: City,
    val cod: String,
    val message: Double,
    val cnt: Int,
    val list: List<WeatherItem> // Danh sách dự báo thời tiết theo từng ngày
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int
)

data class Coord(
    val lon: Double,
    val lat: Double
)

// Thầy giáo gọi class này là WeatherObject, ta đặt là WeatherItem cho chuẩn nghĩa 1 phần tử trong danh sách
data class WeatherItem(
    val dt: Int, // Thời gian (Unix timestamp)
    val sunrise: Int,
    val sunset: Int,
    val temp: Temp, // Nhiệt độ

    @SerializedName("feels_like") // Ánh xạ từ JSON "feels_like" sang biến Kotlin "feelsLike"
    val feelsLike: FeelsLike,

    val pressure: Int,
    val humidity: Int,
    val weather: List<WeatherObject>, // Thông tin thời tiết (Mây, mưa, icon...)
    val speed: Double,
    val deg: Int,
    val gust: Double,
    val clouds: Int,
    val pop: Double
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class WeatherObject(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)