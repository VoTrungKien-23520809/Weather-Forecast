package com.kienvo.jetweatherforecast.ui.theme

import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════
// JetWeather Forecast — Centralized Design Tokens
// Mọi màu sắc trong app được quản lý tại đây duy nhất.
// KHÔNG được khai báo lại ở các Screen file.
// ══════════════════════════════════════════════════════════

// Material 3 Base Colors (giữ lại cho Theme.kt)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ── Glassmorphism Tokens ────────────────────────────────
object GlassTokens {
    val Background = Color.White.copy(alpha = 0.12f)
    val BackgroundMedium = Color.White.copy(alpha = 0.15f)
    val Border = Color.White.copy(alpha = 0.25f)
    val BorderSubtle = Color.White.copy(alpha = 0.10f)
}

// ── Default Weather Gradient (Fallback) ─────────────────
object WeatherColors {
    val GradientStart = Color(0xFF1A237E)   // Xanh đêm sâu thẳm
    val GradientEnd = Color(0xFF26C6DA)     // Xanh lơ dịu mát

    // Icon accent colors
    val SunriseGold = Color(0xFFFFC400)
    val NightBlue = Color(0xFF90CAF9)
    val DeleteRed = Color(0xFFFF8A80)
    val FavoriteRed = Color(0xFFFF5252)

    // Menu & Dropdown
    val MenuBackground = Color(0xFF1E293B)

    // Text on gradient
    val TextPrimary = Color.White
    val TextSecondary = Color.White.copy(alpha = 0.8f)
    val TextTertiary = Color.White.copy(alpha = 0.65f)
    val TextHint = Color.White.copy(alpha = 0.5f)
    val TextDisabled = Color.White.copy(alpha = 0.3f)
}