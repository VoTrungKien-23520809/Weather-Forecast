package com.kienvo.jetweatherforecast.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kienvo.jetweatherforecast.data.local.Unit
import com.kienvo.jetweatherforecast.screens.components.WeatherAppBar
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import com.kienvo.jetweatherforecast.utils.bounceClick

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val unitList by settingsViewModel.unitList.collectAsStateWithLifecycle()

    val activeSettings = if (unitList.isEmpty()) {
        Unit(unit = "metric", windUnit = "m/s", timeFormat = "24h")
    } else {
        unitList.first()
    }

    var unitToggleState by remember(activeSettings) {
        mutableStateOf(activeSettings.unit == "imperial")
    }

    var windToggleState by remember(activeSettings) {
        mutableStateOf(activeSettings.windUnit == "km/h")
    }

    var timeToggleState by remember(activeSettings) {
        mutableStateOf(activeSettings.timeFormat == "12h")
    }

    // Animate accent color when toggling temperature
    val accentColor by animateColorAsState(
        targetValue = if (unitToggleState) Color(0xFFFF7043) else WeatherColors.GradientEnd,
        animationSpec = tween(400),
        label = "accentColor"
    )

    // Hàm gọi lưu đồng bộ toàn bộ cài đặt
    val onSettingsChanged = { isImperial: Boolean, isKmH: Boolean, is12H: Boolean ->
        val updatedUnit = Unit(
            unit = if (isImperial) "imperial" else "metric",
            windUnit = if (isKmH) "km/h" else "m/s",
            timeFormat = if (is12H) "12h" else "24h",
            geminiApiKey = activeSettings.geminiApiKey
        )
        settingsViewModel.updateSettingsUnit(updatedUnit)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val onSaveApiKey = { apiKey: String ->
        val updatedUnit = Unit(
            unit = if (unitToggleState) "imperial" else "metric",
            windUnit = if (windToggleState) "km/h" else "m/s",
            timeFormat = if (timeToggleState) "12h" else "24h",
            geminiApiKey = apiKey.trim()
        )
        settingsViewModel.updateSettingsUnit(updatedUnit)
        android.widget.Toast.makeText(context, "Gemini API Key saved successfully! 🚀", android.widget.Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(WeatherColors.GradientStart, WeatherColors.GradientEnd)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                WeatherAppBar(
                    title = "Settings",
                    isMainScreen = false,
                    navController = navController
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Personalize Your Weather Experience",
                    style = MaterialTheme.typography.titleMedium,
                    color = WeatherColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 1. ĐƠN VỊ NHIỆT ĐỘ
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassTokens.Border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (unitToggleState) "Fahrenheit ºF" else "Celsius ºC",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Temperature unit",
                                style = MaterialTheme.typography.bodySmall,
                                color = WeatherColors.TextTertiary
                            )
                        }

                        Switch(
                            checked = unitToggleState,
                            onCheckedChange = { isChecked ->
                                unitToggleState = isChecked
                                onSettingsChanged(isChecked, windToggleState, timeToggleState)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = accentColor,
                                uncheckedThumbColor = Color.LightGray,
                                uncheckedTrackColor = WeatherColors.TextDisabled
                            )
                        )
                    }
                }

                // 2. ĐƠN VỊ TỐC ĐỘ GIÓ
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassTokens.Border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (windToggleState) "Kilometers/hour (km/h)" else "Meters/second (m/s)",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Wind speed unit",
                                style = MaterialTheme.typography.bodySmall,
                                color = WeatherColors.TextTertiary
                            )
                        }

                        Switch(
                            checked = windToggleState,
                            onCheckedChange = { isChecked ->
                                windToggleState = isChecked
                                onSettingsChanged(unitToggleState, isChecked, timeToggleState)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = accentColor,
                                uncheckedThumbColor = Color.LightGray,
                                uncheckedTrackColor = WeatherColors.TextDisabled
                            )
                        )
                    }
                }

                // 3. ĐỊNH DẠNG THỜI GIAN
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassTokens.Border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (timeToggleState) "12-Hour (AM/PM)" else "24-Hour",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sunrise / Sunset time format",
                                style = MaterialTheme.typography.bodySmall,
                                color = WeatherColors.TextTertiary
                            )
                        }

                        Switch(
                            checked = timeToggleState,
                            onCheckedChange = { isChecked ->
                                timeToggleState = isChecked
                                onSettingsChanged(unitToggleState, windToggleState, isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = accentColor,
                                uncheckedThumbColor = Color.LightGray,
                                uncheckedTrackColor = WeatherColors.TextDisabled
                            )
                        )
                    }
                }

                // 4. CẤU HÌNH GOOGLE GEMINI AI
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassTokens.Border)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Google Gemini AI Integration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter your Gemini API key to activate smart, personalized real-time styling & activity advice.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WeatherColors.TextTertiary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        var geminiApiKeyInput by remember(activeSettings.geminiApiKey) {
                            mutableStateOf(activeSettings.geminiApiKey)
                        }

                        OutlinedTextField(
                            value = geminiApiKeyInput,
                            onValueChange = { geminiApiKeyInput = it },
                            placeholder = {
                                Text(
                                    text = "Enter AI API Key...",
                                    color = WeatherColors.TextDisabled
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = GlassTokens.Border,
                                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                                unfocusedContainerColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .bounceClick {
                                    onSaveApiKey(geminiApiKeyInput)
                                }
                                .background(accentColor, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save API Key",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassTokens.Background.copy(alpha = 0.06f)),
                ) {
                    Text(
                        text = "💡 Settings will be applied immediately across all weather panels and metrics in the dashboard.",
                        style = MaterialTheme.typography.bodySmall,
                        color = WeatherColors.TextTertiary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}