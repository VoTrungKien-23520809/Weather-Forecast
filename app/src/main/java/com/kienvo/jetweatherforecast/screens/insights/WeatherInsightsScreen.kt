package com.kienvo.jetweatherforecast.screens.insights

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kienvo.jetweatherforecast.data.model.WeatherItem
import com.kienvo.jetweatherforecast.data.model.WeatherResponse
import com.kienvo.jetweatherforecast.data.wrapper.Resource
import com.kienvo.jetweatherforecast.screens.components.WeatherAppBar
import com.kienvo.jetweatherforecast.screens.navigation.WeatherScreens
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import com.kienvo.jetweatherforecast.ui.theme.WeatherThemeUtils
import com.kienvo.jetweatherforecast.utils.bounceClick

@Composable
fun WeatherInsightsScreen(
    navController: NavController,
    city: String,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val weatherState by viewModel.weatherData.collectAsStateWithLifecycle()
    val activeSettings by viewModel.activeSettings.collectAsStateWithLifecycle()
    val aiInsightsState by viewModel.aiInsights.collectAsStateWithLifecycle()
    val unitSymbol = if (activeSettings.unit == "imperial") "ºF" else "ºC"

    val iconCode = when (weatherState) {
        is Resource.Success -> {
            (weatherState as Resource.Success<WeatherResponse>).data?.list
                ?.firstOrNull()?.weather?.firstOrNull()?.icon
        }
        else -> null
    }

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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is Resource.Success -> {
                val weatherData = state.data
                val todayWeather = weatherData?.list?.firstOrNull()

                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { isVisible = true }

                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        WeatherAppBar(
                            title = "Weather Insights",
                            isMainScreen = false,
                            navController = navController
                        )
                    }
                ) { paddingValues ->
                    if (todayWeather != null && weatherData != null) {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(animationSpec = tween(800)) +
                                    slideInVertically(animationSpec = tween(800), initialOffsetY = { 80 }),
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            InsightsContent(
                                cityName = weatherData.city.name,
                                weatherItem = todayWeather,
                                unitSymbol = unitSymbol,
                                aiInsightsState = aiInsightsState,
                                navController = navController
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "Đã xảy ra lỗi", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InsightsContent(
    cityName: String,
    weatherItem: WeatherItem,
    unitSymbol: String,
    aiInsightsState: Resource<AiInsightResponse>?,
    navController: NavController
) {
    val tempVal = weatherItem.temp.day.toInt()
    val isImperial = unitSymbol == "ºF"
    val isHot = if (isImperial) tempVal > 82 else tempVal > 28
    val isCold = if (isImperial) tempVal < 64 else tempVal < 18

    val desc = weatherItem.weather.firstOrNull()?.main ?: "Clear"
    val isRainy = desc.contains("rain", ignoreCase = true) ||
            desc.contains("drizzle", ignoreCase = true) ||
            weatherItem.weather.firstOrNull()?.icon?.startsWith("09") == true ||
            weatherItem.weather.firstOrNull()?.icon?.startsWith("10") == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Khối Giới Thiệu Tổng Quan
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .bounceClick { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
            border = BorderStroke(1.2.dp, GlassTokens.Border)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (aiInsightsState is Resource.Success) Icons.Rounded.AutoAwesome else Icons.Rounded.Lightbulb,
                    contentDescription = "Tips Overview",
                    tint = if (aiInsightsState is Resource.Success) Color(0xFFFFD54F) else Color(0xFFFBC02D),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$cityName Insights",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        if (aiInsightsState is Resource.Success) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8A65))
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = "AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI POWERED",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "It's $tempVal$unitSymbol & ${weatherItem.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }}. Here are your custom lifestyle recommendations:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WeatherColors.TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (aiInsightsState) {
            is Resource.Loading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
                    border = BorderStroke(1.2.dp, GlassTokens.Border)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFFD54F),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Consulting Gemini AI Coach...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Generating tailored fashion & activity insights for today's weather.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WeatherColors.TextTertiary
                        )
                    }
                }
            }
            is Resource.Success -> {
                val aiData = aiInsightsState.data!!

                InsightDetailCard(
                    title = aiData.fashion_title ?: "Fashion Advice",
                    icon = Icons.Rounded.Checkroom,
                    iconColor = Color(0xFFFF8A65),
                    recommendations = aiData.fashion_recs ?: emptyList(),
                    reason = aiData.fashion_reason ?: "Appropriate for current conditions."
                )

                Spacer(modifier = Modifier.height(20.dp))

                InsightDetailCard(
                    title = aiData.health_title ?: "Health & Activities",
                    icon = Icons.Rounded.LocalHospital,
                    iconColor = Color(0xFF81C784),
                    recommendations = aiData.health_recs ?: emptyList(),
                    reason = aiData.health_reason ?: "Stay active and stay safe."
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
            is Resource.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
                    border = BorderStroke(1.2.dp, Color(0xFFEF5350).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = "Error",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AI Generation Failed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = aiInsightsState.message ?: "Unknown error occurred.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFEF5350).copy(alpha = 0.9f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .bounceClick {
                                    navController.navigate(WeatherScreens.SettingsScreen.name)
                                }
                                .background(Color(0xFFEF5350).copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Check API Key in Settings", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            else -> {
                // Fallback to static Offline mode
                val fashionTitle: String
                val fashionRecs: List<String>
                val fashionReason: String
                val fashionIcon = Icons.Rounded.Checkroom

                when {
                    isRainy -> {
                        fashionTitle = "Rainy Day Protection"
                        fashionRecs = listOf("Waterproof raincoat or windbreaker", "Pocket umbrella", "Water-resistant sneakers or boots")
                        fashionReason = "Rainy conditions require highly water-resistant layers to stay dry and comfortable."
                    }
                    isHot -> {
                        fashionTitle = "Hot Summer Comfort"
                        fashionRecs = listOf("Light t-shirt & linen shorts", "UV-protection sunglasses", "Wide-brimmed sun hat")
                        fashionReason = "High temperatures require breathable, light-colored fabrics and direct UV shade."
                    }
                    isCold -> {
                        fashionTitle = "Winter Warm Layering"
                        fashionRecs = listOf("Heavy woolen coat or thermal puffer", "Thick scarf & gloves", "Insulated leather boots")
                        fashionReason = "Chilly atmosphere calls for multiple insulating layers to preserve body heat."
                    }
                    else -> {
                        fashionTitle = "Pleasant Smart-Casual"
                        fashionRecs = listOf("Classic denim jacket or cardigan", "Cotton t-shirt & long chinos", "Breathable daily sneakers")
                        fashionReason = "Perfect mild weather allows stylish smart layering and comfortable daily wear."
                    }
                }

                InsightDetailCard(
                    title = fashionTitle,
                    icon = fashionIcon,
                    iconColor = Color(0xFFFF8A65),
                    recommendations = fashionRecs,
                    reason = fashionReason
                )

                Spacer(modifier = Modifier.height(20.dp))

                val healthTitle = "Health & Activities"
                val healthRecs = mutableListOf<String>()
                val healthReason: String

                if (isRainy || weatherItem.pop > 0.4) {
                    healthRecs.add("Avoid outdoor runs; try indoor yoga or home gym workouts.")
                    healthRecs.add("Check indoors humidity to prevent allergen/mold accumulation.")
                    healthReason = "High probability of rain suggests focusing on indoor wellness and domestic activities."
                } else {
                    healthRecs.add("Excellent weather for cycling, jogging, or an outdoor cafe date.")
                    healthRecs.add("Open windows to refresh room ventilation naturally.")
                    healthReason = "Clear and calm atmospheric conditions are highly suitable for active outdoor lifestyles."
                }

                if (isHot) {
                    healthRecs.add("Drink at least 2.5L - 3L of water today to prevent dehydration.")
                    healthRecs.add("Apply SPF 30+ sunscreen even under brief light exposure.")
                } else {
                    healthRecs.add("Keep drinking warm tea or water to maintain normal metabolism.")
                }

                InsightDetailCard(
                    title = healthTitle,
                    icon = Icons.Rounded.LocalHospital,
                    iconColor = Color(0xFF81C784),
                    recommendations = healthRecs,
                    reason = healthReason
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .bounceClick {
                            navController.navigate(WeatherScreens.SettingsScreen.name)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, GlassTokens.Border.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "💡 Nhập Gemini API Key trong Settings để kích hoạt phân tích AI cá nhân hoá độc quyền.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WeatherColors.TextTertiary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InsightDetailCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    recommendations: List<String>,
    reason: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
        border = BorderStroke(1.2.dp, GlassTokens.Border)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                recommendations.forEach { rec ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Done",
                            tint = iconColor.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = rec,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = GlassTokens.BorderSubtle)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "💡 Advice: $reason",
                fontSize = 13.sp,
                color = WeatherColors.TextTertiary,
                fontWeight = FontWeight.Light,
                lineHeight = 18.sp
            )
        }
    }
}
