package com.kienvo.jetweatherforecast.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kienvo.jetweatherforecast.data.model.WeatherItem
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import com.kienvo.jetweatherforecast.utils.bounceClick

@Composable
fun AdvancedWeatherDetailCard(weatherItem: WeatherItem, isImperial: Boolean) {
    val windUnit = if (isImperial) "mph" else "m/s"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
        border = BorderStroke(1.2.dp, GlassTokens.Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Chance of Rain (POP)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Grain,
                    contentDescription = "Chance of Rain",
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(weatherItem.pop * 100).toInt()}%",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Rain Chance",
                    style = MaterialTheme.typography.bodySmall,
                    color = WeatherColors.TextTertiary
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(44.dp)
                    .width(1.dp),
                color = GlassTokens.Border
            )

            // 2. Cloudiness
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.CloudQueue,
                    contentDescription = "Cloudiness",
                    tint = Color(0xFFB0BEC5),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${weatherItem.clouds}%",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Cloudiness",
                    style = MaterialTheme.typography.bodySmall,
                    color = WeatherColors.TextTertiary
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(44.dp)
                    .width(1.dp),
                color = GlassTokens.Border
            )

            // 3. Wind Gust
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Air,
                    contentDescription = "Wind Gust",
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${weatherItem.gust.toInt()} $windUnit",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "Wind Gust",
                    style = MaterialTheme.typography.bodySmall,
                    color = WeatherColors.TextTertiary
                )
            }
        }
    }
}
