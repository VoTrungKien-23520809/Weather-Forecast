package com.kienvo.jetweatherforecast.screens.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.jetweatherforecast.data.model.WeatherResponse
import com.kienvo.jetweatherforecast.data.wrapper.Resource
import com.kienvo.jetweatherforecast.repository.SettingsRepository
import com.kienvo.jetweatherforecast.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _weatherData = MutableStateFlow<Resource<WeatherResponse>>(Resource.Loading())
    val weatherData: StateFlow<Resource<WeatherResponse>> = _weatherData.asStateFlow()

    // Expose đơn vị đo hiện tại để UI nắm bắt (ºC hay ºF)
    val isImperial: StateFlow<Boolean> = settingsRepository.getUnits()
        .map { list -> list.isNotEmpty() && list.first().unit == "imperial" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Expose toàn bộ cài đặt hiện tại của người dùng
    val activeSettings: StateFlow<com.kienvo.jetweatherforecast.data.local.Unit> = settingsRepository.getUnits()
        .map { list -> 
            if (list.isEmpty()) {
                com.kienvo.jetweatherforecast.data.local.Unit(unit = "metric", windUnit = "m/s", timeFormat = "24h")
            } else {
                list.first()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.kienvo.jetweatherforecast.data.local.Unit(unit = "metric", windUnit = "m/s", timeFormat = "24h")
        )

    // Lưu lại tên thành phố để có thể refresh khi đổi đơn vị
    private var currentCity: String = ""

    init {
        currentCity = savedStateHandle.get<String>("city") ?: "Ho Chi Minh"
        
        // Theo dõi sự thay đổi của đơn vị đo trong Room và tự động cập nhật thời tiết!
        viewModelScope.launch {
            settingsRepository.getUnits().collect { unitList ->
                val unit = if (unitList.isEmpty()) "metric" else unitList.first().unit
                getWeatherData(currentCity, unit)
            }
        }
    }

    private fun getWeatherData(city: String, unit: String) {
        viewModelScope.launch {
            _weatherData.value = Resource.Loading()
            try {
                val response = repository.getWeather(cityQuery = city, units = unit)
                _weatherData.value = response
            } catch (e: Exception) {
                _weatherData.value = Resource.Error("Lỗi: ${e.message}")
            }
        }
    }

    /** Gọi lại khi cần refresh */
    fun refreshWeather() {
        if (currentCity.isNotBlank()) {
            viewModelScope.launch {
                val unitList = settingsRepository.getUnits().first()
                val unit = if (unitList.isEmpty()) "metric" else unitList.first().unit
                getWeatherData(currentCity, unit)
            }
        }
    }
}