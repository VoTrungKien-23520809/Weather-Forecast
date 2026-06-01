package com.kienvo.jetweatherforecast.repository

import com.kienvo.jetweatherforecast.data.model.CitySuggestion
import com.kienvo.jetweatherforecast.data.model.WeatherResponse
import com.kienvo.jetweatherforecast.data.network.WeatherApi
import com.kienvo.jetweatherforecast.data.wrapper.Resource
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val api: WeatherApi) {

    suspend fun getWeather(cityQuery: String, units: String = "metric"): Resource<WeatherResponse> {
        return try {
            val response = api.getWeather(query = cityQuery, units = units)
            Resource.Success(data = response)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "An error occurred")
        }
    }

    suspend fun getCitySuggestions(query: String): List<CitySuggestion> {
        return try {
            api.searchCity(query = query)
        } catch (e: Exception) {
            emptyList()
        }
    }
}