package com.kienvo.jetweatherforecast.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kienvo.jetweatherforecast.screens.navigation.WeatherScreens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import kotlinx.coroutines.delay

@Composable
fun WeatherSplashScreen(navController: NavController) {

    // ── GIAO DIỆN CHUYỂN ĐỘNG BAN ĐẦU (INTRO) ──────────────────
    val scale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val taglineOffset = remember { Animatable(30f) }

    LaunchedEffect(key1 = true) {
        // Hoạt ảnh Logo bung ra với hiệu ứng Overshoot lò xo
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = { OvershootInterpolator(3.5f).getInterpolation(it) }
            )
        )
        // Chữ trượt từ dưới lên & hiện dần
        textAlpha.animateTo(1f, animationSpec = tween(800))
        taglineOffset.animateTo(0f, animationSpec = tween(800))

        // Chờ hoàn thành hoạt ảnh và điều hướng
        delay(2800L)
        val defaultCity = "Ho Chi Minh"
        navController.navigate("${WeatherScreens.MainScreen.name}/$defaultCity") {
            popUpTo(WeatherScreens.SplashScreen.name) { inclusive = true }
        }
    }

    // ── HIỆU ỨNG HOẠT ẢNH VÔ HẠN (INFINITE ANIMATIONS) ──────────
    val infiniteTransition = rememberInfiniteTransition(label = "weatherSplashInfinite")

    // 1. Flowing background gradient shifting
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    // 2. Soft Glow Aura breathing effect
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // 3. Rotating Outer Glassmorphism Ring
    val rotationDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationDegrees"
    )

    // 4. Sinusoidal Floating Weather Icon
    val logoFloatOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoFloatOffset"
    )

    // 5. Sun icon gentle pulse
    val sunPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunPulseScale"
    )

    // Nền dải màu chuyển động nghệ thuật
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0D0D2B), // Deep Space Blue
            Color(0xFF1D1B4B), // Premium Violet
            Color(0xFF3B0764), // Rich Royal Purple
            Color(0xFF1E1B4B)  // Midnight Indigo
        ),
        start = Offset(0f, 0f),
        end = Offset(1200f * (1f + gradientOffset), 1200f * (2f - gradientOffset))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Lớp 1: Soft Glow Aura
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(glowScale)
                        .alpha(glowAlpha)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF8A65).copy(alpha = 0.45f), // Cam hoàng hôn phát sáng
                                    Color(0xFF8E24AA).copy(alpha = 0.15f), // Tím phản quang
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Lớp 2: Vòng kính mờ ngoài xoay chậm (Sweep Gradient border)
                Surface(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale.value)
                        .rotate(rotationDegrees),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.04f),
                    border = BorderStroke(
                        width = 1.2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.05f),
                                Color.White.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.05f),
                                Color.White.copy(alpha = 0.6f)
                            )
                        )
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize())
                }

                // Lớp 3: Vòng kính mờ nổi bên trong + Icon thời tiết lơ lửng & co giãn
                Surface(
                    modifier = Modifier
                        .size(136.dp)
                        .scale(scale.value)
                        .offset(y = logoFloatOffset.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(
                        width = 0.6.dp,
                        color = Color.White.copy(alpha = 0.15f)
                    ),
                    shadowElevation = 8.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WbSunny,
                            contentDescription = "Sun Icon",
                            modifier = Modifier
                                .size(72.dp)
                                .scale(sunPulseScale),
                            tint = WeatherColors.SunriseGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // App Name với font cao cấp & trượt nhẹ
            Text(
                text = "JetWeather",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .offset(y = taglineOffset.value.dp * 0.5f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline chỉ dẫn trượt lên thanh lịch
            Text(
                text = "Premium Weather Companion",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .offset(y = taglineOffset.value.dp)
            )
        }
    }
}