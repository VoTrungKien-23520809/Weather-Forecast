package com.kienvo.jetweatherforecast.screens.insights

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.kienvo.jetweatherforecast.data.local.Unit
import com.kienvo.jetweatherforecast.data.model.WeatherResponse
import com.kienvo.jetweatherforecast.data.wrapper.Resource
import com.kienvo.jetweatherforecast.repository.SettingsRepository
import com.kienvo.jetweatherforecast.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _weatherData = MutableStateFlow<Resource<WeatherResponse>>(Resource.Loading())
    val weatherData: StateFlow<Resource<WeatherResponse>> = _weatherData.asStateFlow()

    private val _aiInsights = MutableStateFlow<Resource<AiInsightResponse>?>(null)
    val aiInsights: StateFlow<Resource<AiInsightResponse>?> = _aiInsights.asStateFlow()

    val activeSettings: StateFlow<Unit> = settingsRepository.getUnits()
        .map { list -> if (list.isEmpty()) Unit(unit = "metric", windUnit = "m/s", timeFormat = "24h") else list.first() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Unit(unit = "metric", windUnit = "m/s", timeFormat = "24h"))

    private var currentCity: String = ""

    init {
        currentCity = savedStateHandle.get<String>("city") ?: "Ho Chi Minh"
        viewModelScope.launch {
            settingsRepository.getUnits().collect { unitList ->
                val activeUnit = if (unitList.isEmpty()) {
                    Unit(unit = "metric", windUnit = "m/s", timeFormat = "24h")
                } else {
                    unitList.first()
                }
                getWeatherData(currentCity, activeUnit)
            }
        }
    }

    private fun getWeatherData(city: String, unitSetting: Unit) {
        viewModelScope.launch {
            _weatherData.value = Resource.Loading()
            try {
                val response = repository.getWeather(cityQuery = city, units = unitSetting.unit)
                _weatherData.value = response
                if (response is Resource.Success && response.data != null) {
                    generateAiInsights(response.data, unitSetting)
                }
            } catch (e: Exception) {
                _weatherData.value = Resource.Error("Lỗi: ${e.message}")
            }
        }
    }

    private fun generateAiInsights(weather: WeatherResponse, unitSetting: Unit) {
        val apiKey = unitSetting.geminiApiKey.trim()
        Log.d("GeminiAI", "generateAiInsights: city=${weather.city.name}, apiKeyLength=${apiKey.length}")
        
        // 1. Kiểm tra Cache trong bộ nhớ RAM trước
        val cached = getCachedInsight(weather.city.name)
        if (cached != null) {
            Log.d("GeminiAI", "generateAiInsights: Found valid cached AI insight for ${weather.city.name}. Skipping API call.")
            _aiInsights.value = Resource.Success(cached)
            return
        }

        if (apiKey.isEmpty()) {
            Log.d("GeminiAI", "generateAiInsights: API key is empty, falling back to offline.")
            _aiInsights.value = null
            return
        }

        viewModelScope.launch {
            _aiInsights.value = Resource.Loading()
            try {
                // Thử gọi model mới nhất của năm 2026: gemini-2.5-flash (Nhanh, tiết kiệm)
                Log.d("GeminiAI", "generateAiInsights: Trying gemini-2.5-flash...")
                val result = callGeminiModel(weather, unitSetting, apiKey, "gemini-2.5-flash")
                saveCachedInsight(weather.city.name, result) // Lưu vào Cache khi thành công
                _aiInsights.value = Resource.Success(result)
            } catch (e: Exception) {
                Log.w("GeminiAI", "generateAiInsights: gemini-2.5-flash failed, trying gemini-2.5-pro fallback...", e)
                try {
                    // Fallback sang bản Pro nếu bản Flash gặp sự cố
                    Log.d("GeminiAI", "generateAiInsights: Trying gemini-2.5-pro fallback...")
                    val result = callGeminiModel(weather, unitSetting, apiKey, "gemini-2.5-pro")
                    saveCachedInsight(weather.city.name, result) // Lưu vào Cache khi thành công
                    _aiInsights.value = Resource.Success(result)
                } catch (e2: Exception) {
                    Log.e("GeminiAI", "generateAiInsights: All fallbacks failed!", e2)
                    _aiInsights.value = Resource.Error("Lỗi kết nối AI: Không thể gọi Gemini API. Vui lòng kiểm tra lại kết nối mạng hoặc API Key.")
                }
            }
        }
    }

    private suspend fun callGeminiModel(
        weather: WeatherResponse,
        unitSetting: Unit,
        apiKey: String,
        modelName: String
    ): AiInsightResponse {
        val prompt = """
            You are an expert fashion advisor and health coach. Analyze the following weather conditions:
            City: ${weather.city.name}
            Temp: ${weather.list.firstOrNull()?.temp?.day} ${if (unitSetting.unit == "imperial") "°F" else "°C"}
            Feels Like: ${weather.list.firstOrNull()?.feelsLike?.day} ${if (unitSetting.unit == "imperial") "°F" else "°C"}
            Humidity: ${weather.list.firstOrNull()?.humidity}%
            Wind: ${weather.list.firstOrNull()?.speed} ${unitSetting.windUnit}
            Condition: ${weather.list.firstOrNull()?.weather?.firstOrNull()?.description}

            Based on these, generate custom, detailed fashion and health/activity recommendations in Vietnamese.
            Return a JSON object with this exact structure (do not wrap in markdown, no backticks, just raw JSON, and ensure no syntax errors):
            {
              "fashion_title": "Tiêu đề ngắn gọn về thời trang (ví dụ: Phối Đồ Năng Động)",
              "fashion_recs": ["gợi ý 1", "gợi ý 2", "gợi ý 3"],
              "fashion_reason": "Giải thích lý do lựa chọn trang phục này",
              "health_title": "Tiêu đề ngắn gọn về sức khoẻ (ví dụ: Bảo Vệ Sức Khoẻ)",
              "health_recs": ["lời khuyên 1", "lời khuyên 2", "lời khuyên 3"],
              "health_reason": "Giải thích lý do cho các lời khuyên sức khoẻ"
            }
        """.trimIndent()

        // JSON mode is only supported on 1.5 models
        val generativeModel = if (modelName.contains("1.5") || modelName.contains("2.") || modelName.contains("3.")) {
            val config = generationConfig {
                responseMimeType = "application/json"
            }
            GenerativeModel(
                modelName = modelName,
                apiKey = apiKey,
                generationConfig = config
            )
        } else {
            GenerativeModel(
                modelName = modelName,
                apiKey = apiKey
            )
        }

        Log.d("GeminiAI", "callGeminiModel: sending request to model '$modelName'...")
        val response = generativeModel.generateContent(prompt)
        val jsonText = response.text ?: ""
        Log.d("GeminiAI", "callGeminiModel: response from model '$modelName': '$jsonText'")

        // Extract JSON between { and } if model returned markdown backticks
        var cleaned = jsonText.trim()
        if (cleaned.startsWith("```") || cleaned.contains("```json")) {
            val startIndex = cleaned.indexOf("{")
            val endIndex = cleaned.lastIndexOf("}")
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                cleaned = cleaned.substring(startIndex, endIndex + 1)
            }
        }

        val gson = Gson()
        val parsed = gson.fromJson(cleaned, AiInsightResponse::class.java)
        if (parsed != null && !parsed.fashion_title.isNullOrEmpty()) {
            return parsed
        } else {
            throw Exception("Dữ liệu phản hồi từ model '$modelName' rỗng hoặc sai cấu trúc JSON.")
        }
    }

    // ── CƠ CHẾ CACHE IN-MEMORY DÀNH CHO GEMINI AI ────────────────────────
    data class CachedAiInsight(
        val cityName: String,
        val response: AiInsightResponse,
        val timestamp: Long
    )

    companion object {
        // Ánh xạ lưu giữ trong tiến trình (lowercase cityName -> CachedAiInsight)
        private val aiCache = mutableMapOf<String, CachedAiInsight>()
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L // 24 tiếng hết hạn

        fun getCachedInsight(cityName: String): AiInsightResponse? {
            val key = cityName.trim().lowercase()
            val cached = aiCache[key] ?: return null
            // Quá 24 tiếng tự động xoá và yêu cầu gen mới
            if (System.currentTimeMillis() - cached.timestamp > CACHE_EXPIRY_MS) {
                Log.d("GeminiAI", "Cache expired for city: $cityName. Removing.")
                aiCache.remove(key)
                return null
            }
            return cached.response
        }

        fun saveCachedInsight(cityName: String, response: AiInsightResponse) {
            val key = cityName.trim().lowercase()
            aiCache[key] = CachedAiInsight(cityName, response, System.currentTimeMillis())
            Log.d("GeminiAI", "Successfully saved AI response to cache for city: $cityName")
        }
    }
}
