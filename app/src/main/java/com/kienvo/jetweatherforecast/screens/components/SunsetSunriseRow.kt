package com.kienvo.jetweatherforecast.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kienvo.jetweatherforecast.data.model.WeatherItem
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Hàm chuyển đổi Unix Timestamp sang giờ (VD: 06:15 AM hoặc 06:15)
fun formatTime(timestamp: Int, timeFormat: String): String {
    val pattern = if (timeFormat == "12h") "hh:mm a" else "HH:mm"
    return Instant.ofEpochSecond(timestamp.toLong())
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH))
}

@Composable
fun SunsetSunriseRow(weatherItem: WeatherItem, timeFormat: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SunEventCard(
            label = "Sunrise",
            time = formatTime(weatherItem.sunrise, timeFormat),
            icon = Icons.Rounded.WbSunny,
            iconColor = WeatherColors.SunriseGold
        )
        SunEventCard(
            label = "Sunset",
            time = formatTime(weatherItem.sunset, timeFormat),
            icon = Icons.Filled.NightsStay,
            iconColor = WeatherColors.NightBlue
        )
    }
}

@Composable
fun RowScope.SunEventCard(label: String, time: String, icon: ImageVector, iconColor: Color) {
    Card(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
        border = BorderStroke(1.dp, GlassTokens.Border)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.TextTertiary
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}