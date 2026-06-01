package com.kienvo.jetweatherforecast.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kienvo.jetweatherforecast.data.model.WeatherItem
import com.kienvo.jetweatherforecast.data.model.WeatherResponse
import com.kienvo.jetweatherforecast.data.wrapper.Resource
import com.kienvo.jetweatherforecast.screens.components.CurrentWeatherDisplay
import com.kienvo.jetweatherforecast.screens.components.SunsetSunriseRow
import com.kienvo.jetweatherforecast.screens.components.WeatherAppBar
import com.kienvo.jetweatherforecast.screens.components.WeatherDetailRow
import com.kienvo.jetweatherforecast.screens.components.AdvancedWeatherDetailCard
import com.kienvo.jetweatherforecast.screens.components.DailyTempTimeline
import com.kienvo.jetweatherforecast.screens.navigation.WeatherScreens
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import com.kienvo.jetweatherforecast.ui.theme.WeatherThemeUtils
import com.kienvo.jetweatherforecast.utils.bounceClick
import kotlinx.coroutines.delay

@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel) {
    val weatherState by viewModel.weatherData.collectAsStateWithLifecycle()
    val isImperial by viewModel.isImperial.collectAsStateWithLifecycle()
    val unitSymbol = if (isImperial) "ºF" else "ºC"

    val activeSettings by viewModel.activeSettings.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Lấy icon code để tạo gradient động
    val iconCode = when (weatherState) {
        is Resource.Success -> {
            (weatherState as Resource.Success<WeatherResponse>).data?.list
                ?.firstOrNull()?.weather?.firstOrNull()?.icon
        }
        else -> null
    }

    // Animate gradient colors cho transition mượt mà
    val animatedColors = WeatherThemeUtils.animateWeatherGradient(iconCode)
    val gradientBrush = Brush.verticalGradient(
        colors = animatedColors.map { it.value }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        when (val state = weatherState) {
            is Resource.Loading -> {
                LoadingState()
            }
            is Resource.Success -> {
                val weatherData = state.data
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { isVisible = true }

                Scaffold(
                    containerColor = Color.Transparent,
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                                .wrapContentWidth()
                        ) { data ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .wrapContentSize()
                                        .bounceClick { },
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.92f)),
                                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color.White.copy(alpha = 0.15f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Favorite",
                                            tint = WeatherColors.FavoriteRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = data.visuals.message,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    },
                    topBar = {
                        WeatherAppBar(
                            title = "${weatherData?.city?.name}, ${weatherData?.city?.country}",
                            isMainScreen = true,
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            onSearchClicked = {
                                navController.navigate(WeatherScreens.SearchScreen.name)
                            }
                        )
                    }
                ) { paddingValues ->
                    weatherData?.list?.firstOrNull()?.let { todayWeather ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(animationSpec = tween(1000)) +
                                    slideInVertically(animationSpec = tween(800), initialOffsetY = { 100 }),
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            MainContent(
                                weatherItem = todayWeather,
                                weatherData = weatherData,
                                unitSymbol = unitSymbol,
                                activeSettings = activeSettings
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                ErrorState(message = state.message ?: "Đã xảy ra lỗi")
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading weather...",
                style = MaterialTheme.typography.bodyLarge,
                color = WeatherColors.TextTertiary
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassTokens.Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = "Error",
                    tint = WeatherColors.DeleteRed,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Oops!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun MainContent(
    weatherItem: WeatherItem,
    weatherData: WeatherResponse,
    unitSymbol: String,
    activeSettings: com.kienvo.jetweatherforecast.data.local.Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        CurrentWeatherDisplay(weatherItem = weatherItem, unitSymbol = unitSymbol)

        Spacer(modifier = Modifier.height(32.dp))

        // 1. Biểu đồ nhiệt độ trong ngày (Daily Temp Timeline)
        DailyTempTimeline(temp = weatherItem.temp, unitSymbol = unitSymbol)

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Khối thông tin phụ chính phong cách Glassmorphism
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassTokens.Border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassInfoItem(label = "Humidity", value = "${weatherItem.humidity}%")
                HorizontalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = GlassTokens.Border
                )
                GlassInfoItem(label = "Pressure", value = "${weatherItem.pressure} hPa")
                HorizontalDivider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = GlassTokens.Border
                )
                
                // Quy đổi tốc độ gió thực tế (Nhân 3.6 khi đổi từ m/s sang km/h)
                val windValStr = if (activeSettings.windUnit == "km/h") {
                    "${(weatherItem.speed * 3.6).toInt()} km/h"
                } else {
                    "${weatherItem.speed.toInt()} m/s"
                }
                GlassInfoItem(label = "Wind", value = windValStr)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Khối thông tin mở rộng nâng cao (Advanced Weather Details)
        AdvancedWeatherDetailCard(weatherItem = weatherItem, isImperial = unitSymbol == "ºF")

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Bình minh / Hoàng hôn
        SunsetSunriseRow(weatherItem = weatherItem, timeFormat = activeSettings.timeFormat)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "7-Day Forecast",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 12.dp)
        )

        // 5. Thẻ danh sách dự báo 7 ngày trượt lên tuần tự (Staggered Animation)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .bounceClick { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassTokens.Border)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                weatherData.list.drop(1).forEachIndexed { index, dailyItem ->
                    var isItemVisible by remember(dailyItem.dt) { mutableStateOf(false) }
                    LaunchedEffect(dailyItem.dt) {
                        delay(index * 60L) // Staggered delay trượt dần
                        isItemVisible = true
                    }
                    AnimatedVisibility(
                        visible = isItemVisible,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(animationSpec = tween(400), initialOffsetY = { 30 }),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        Column {
                            WeatherDetailRow(weatherItem = dailyItem, unitSymbol = unitSymbol)
                            if (index < weatherData.list.drop(1).size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = GlassTokens.BorderSubtle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = WeatherColors.TextTertiary
        )
    }
}
