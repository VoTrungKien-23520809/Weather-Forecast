package com.kienvo.jetweatherforecast.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kienvo.jetweatherforecast.data.model.WeatherItem
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeatherDetailRow(weatherItem: WeatherItem, unitSymbol: String) {
    val date = Instant.ofEpochSecond(weatherItem.dt.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))

    val iconCode = weatherItem.weather.firstOrNull()?.icon ?: "01d"
    val imageUrl = "https://openweathermap.org/img/wn/${iconCode}@4x.png"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = dayOfWeek.take(3),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        AsyncImage(
            model = imageUrl,
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(40.dp)
                .weight(0.8f)
        )

        Text(
            text = weatherItem.weather.firstOrNull()?.main ?: "Clear",
            style = MaterialTheme.typography.bodyMedium,
            color = WeatherColors.TextTertiary,
            modifier = Modifier.weight(1.2f)
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${weatherItem.temp.max.toInt()}$unitSymbol",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${weatherItem.temp.min.toInt()}$unitSymbol",
                style = MaterialTheme.typography.titleMedium,
                color = WeatherColors.TextHint
            )
        }
    }
}