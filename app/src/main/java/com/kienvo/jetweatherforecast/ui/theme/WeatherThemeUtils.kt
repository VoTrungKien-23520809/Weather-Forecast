package com.kienvo.jetweatherforecast.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Ánh xạ icon code từ OpenWeatherMap API sang Gradient phù hợp.
 *
 * Icon code format: "XXd" hoặc "XXn" (d = day, n = night)
 * XX codes:
 * - 01 = Clear sky
 * - 02 = Few clouds
 * - 03 = Scattered clouds
 * - 04 = Broken/Overcast clouds
 * - 09 = Shower rain
 * - 10 = Rain
 * - 11 = Thunderstorm
 * - 13 = Snow
 * - 50 = Mist/Fog
 */
object WeatherThemeUtils {

    // ── Day Gradients ───────────────────────────────────
    private val ClearDayGradient = listOf(
        Color(0xFF2196F3),  // Xanh da trời tươi sáng
        Color(0xFF64B5F6),  // Xanh nhạt giữa
        Color(0xFFF9A825),  // Ánh vàng mặt trời phía dưới
    )

    private val FewCloudsDayGradient = listOf(
        Color(0xFF1E88E5),
        Color(0xFF64B5F6),
        Color(0xFF90CAF9),
    )

    private val CloudyDayGradient = listOf(
        Color(0xFF546E7A),  // Xám thép
        Color(0xFF78909C),
        Color(0xFF90A4AE),
    )

    private val OvercastGradient = listOf(
        Color(0xFF37474F),
        Color(0xFF546E7A),
        Color(0xFF78909C),
    )

    private val RainGradient = listOf(
        Color(0xFF1A237E),  // Xanh đậm mưa
        Color(0xFF283593),
        Color(0xFF42A5F5),  // Xanh nước mưa
    )

    private val ThunderstormGradient = listOf(
        Color(0xFF0D0D1A),  // Tím đen bão tố
        Color(0xFF1A1A2E),
        Color(0xFF4A148C),  // Tím sét đánh
    )

    private val SnowGradient = listOf(
        Color(0xFFB0BEC5),  // Xám tuyết nhạt
        Color(0xFFCFD8DC),
        Color(0xFFECEFF1),  // Trắng tuyết
    )

    private val MistGradient = listOf(
        Color(0xFF607D8B),  // Xám sương mù
        Color(0xFF90A4AE),
        Color(0xFFB0BEC5),
    )

    // ── Night Gradients ─────────────────────────────────
    private val ClearNightGradient = listOf(
        Color(0xFF0D0D2B),  // Đen vũ trụ
        Color(0xFF1A1A4E),  // Tím đêm
        Color(0xFF302B63),  // Tím sâu
    )

    private val CloudyNightGradient = listOf(
        Color(0xFF1A1A2E),
        Color(0xFF2D2D44),
        Color(0xFF3E3E5E),
    )

    private val RainNightGradient = listOf(
        Color(0xFF0A0A1A),
        Color(0xFF0D1B2A),
        Color(0xFF1B3A4B),
    )

    // ── Default / Fallback ──────────────────────────────
    private val DefaultGradient = listOf(
        Color(0xFF1A237E),
        Color(0xFF1565C0),
        Color(0xFF26C6DA),
    )

    /**
     * Trả về danh sách màu gradient dựa trên icon code.
     */
    fun getWeatherGradient(iconCode: String?): List<Color> {
        if (iconCode.isNullOrBlank()) return DefaultGradient

        val isNight = iconCode.endsWith("n")
        val conditionCode = iconCode.take(2)

        return when (conditionCode) {
            "01" -> if (isNight) ClearNightGradient else ClearDayGradient
            "02" -> if (isNight) CloudyNightGradient else FewCloudsDayGradient
            "03" -> if (isNight) CloudyNightGradient else CloudyDayGradient
            "04" -> if (isNight) CloudyNightGradient else OvercastGradient
            "09" -> if (isNight) RainNightGradient else RainGradient
            "10" -> if (isNight) RainNightGradient else RainGradient
            "11" -> ThunderstormGradient // Bão cả ngày lẫn đêm đều tối
            "13" -> SnowGradient         // Tuyết giữ tone nhạt cả ngày đêm
            "50" -> MistGradient
            else -> DefaultGradient
        }
    }

    /**
     * Tạo Brush.verticalGradient từ icon code.
     */
    fun getWeatherBrush(iconCode: String?): Brush {
        return Brush.verticalGradient(colors = getWeatherGradient(iconCode))
    }

    /**
     * Animate gradient colors cho transition mượt mà khi data thay đổi.
     */
    @Composable
    fun animateWeatherGradient(iconCode: String?): List<State<Color>> {
        val targetColors = getWeatherGradient(iconCode)
        return targetColors.map { targetColor ->
            animateColorAsState(
                targetValue = targetColor,
                animationSpec = tween(durationMillis = 1500),
                label = "weatherGradient"
            )
        }
    }
}
