package com.kienvo.jetweatherforecast.screens.components

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kienvo.jetweatherforecast.data.model.WeatherItem
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CurrentWeatherDisplay(weatherItem: WeatherItem, unitSymbol: String) {
    // Định dạng ngày tháng
    val date = Instant.ofEpochSecond(weatherItem.dt.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val formattedDate = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH))

    // Lấy link icon thời tiết chất lượng cao
    val iconCode = weatherItem.weather.firstOrNull()?.icon ?: "01d"
    val imageUrl = "https://openweathermap.org/img/wn/${iconCode}@4x.png"

    // Hiệu ứng pulse nhẹ cho icon thời tiết
    val infiniteTransition = rememberInfiniteTransition(label = "weatherIconPulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. Ngày tháng thanh lịch
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyLarge,
            color = WeatherColors.TextTertiary,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Icon thời tiết với hiệu ứng pulse
        AsyncImage(
            model = imageUrl,
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(160.dp)
                .scale(iconScale)
        )

        // 3. Số đo nhiệt độ lớn - AnimatedContent cuộn mượt mà
        AnimatedContent(
            targetState = "${weatherItem.temp.day.toInt()}$unitSymbol",
            transitionSpec = {
                (slideInVertically(animationSpec = tween(500, easing = EaseInOut)) { it } + fadeIn(animationSpec = tween(500))) togetherWith
                (slideOutVertically(animationSpec = tween(500, easing = EaseInOut)) { -it } + fadeOut(animationSpec = tween(300)))
            },
            label = "mainTempAnim"
        ) { tempText ->
            Text(
                text = tempText,
                fontSize = 100.sp,
                fontWeight = FontWeight.ExtraLight,
                color = Color.White,
                modifier = Modifier.padding(start = 18.dp)
            )
        }

        // 4. Min / Max temperature - AnimatedContent
        AnimatedContent(
            targetState = Pair(weatherItem.temp.max.toInt(), weatherItem.temp.min.toInt()),
            transitionSpec = {
                (slideInVertically(animationSpec = tween(450)) { it } + fadeIn(animationSpec = tween(450))) togetherWith
                (slideOutVertically(animationSpec = tween(450)) { -it } + fadeOut(animationSpec = tween(250)))
            },
            label = "minMaxTempAnim"
        ) { (maxTemp, minTemp) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "H:$maxTemp$unitSymbol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = WeatherColors.TextSecondary
                )
                Text(
                    text = "L:$minTemp$unitSymbol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    color = WeatherColors.TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 5. Feels Like - AnimatedContent
        AnimatedContent(
            targetState = weatherItem.feelsLike.day.toInt(),
            transitionSpec = {
                (slideInVertically(animationSpec = tween(400)) { it } + fadeIn(animationSpec = tween(400))) togetherWith
                (slideOutVertically(animationSpec = tween(400)) { -it } + fadeOut(animationSpec = tween(200)))
            },
            label = "feelsLikeAnim"
        ) { feelsLikeTemp ->
            Text(
                text = "Feels like $feelsLikeTemp$unitSymbol",
                style = MaterialTheme.typography.bodyMedium,
                color = WeatherColors.TextHint
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6. Trạng thái thời tiết
        Text(
            text = weatherItem.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
            style = MaterialTheme.typography.titleLarge,
            color = WeatherColors.TextSecondary,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.5.sp
        )
    }
}