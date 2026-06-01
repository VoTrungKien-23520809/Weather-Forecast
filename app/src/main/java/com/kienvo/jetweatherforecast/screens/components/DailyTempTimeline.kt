package com.kienvo.jetweatherforecast.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kienvo.jetweatherforecast.data.model.Temp
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import com.kienvo.jetweatherforecast.utils.bounceClick

@Composable
fun DailyTempTimeline(temp: Temp, unitSymbol: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
        border = BorderStroke(1.2.dp, GlassTokens.Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hourly Temperature Curve",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 8.dp, bottom = 16.dp)
            )

            // 1. Column header (Morn, Day, Eve, Night)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimelineHeaderItem(
                    label = "Morn",
                    icon = Icons.Rounded.WbTwilight,
                    iconColor = Color(0xFFFFA726),
                    temp = "${temp.morn.toInt()}$unitSymbol"
                )
                TimelineHeaderItem(
                    label = "Day",
                    icon = Icons.Rounded.WbSunny,
                    iconColor = Color(0xFFFFEE58),
                    temp = "${temp.day.toInt()}$unitSymbol"
                )
                TimelineHeaderItem(
                    label = "Eve",
                    icon = Icons.Rounded.WbTwilight,
                    iconColor = Color(0xFFFB8C00),
                    temp = "${temp.eve.toInt()}$unitSymbol"
                )
                TimelineHeaderItem(
                    label = "Night",
                    icon = Icons.Rounded.NightsStay,
                    iconColor = Color(0xFF90CAF9),
                    temp = "${temp.night.toInt()}$unitSymbol"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Custom Bezier Curve temperature visualizer on Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 24.dp)
            ) {
                val width = size.width
                val height = size.height

                val temps = listOf(temp.morn, temp.day, temp.eve, temp.night)
                val minTemp = temps.minOrNull() ?: 0.0
                val maxTemp = temps.maxOrNull() ?: 1.0
                val tempRange = (maxTemp - minTemp).coerceAtLeast(1.0)

                // Calculate positions of each point
                val points = temps.mapIndexed { index, t ->
                    val x = (index * 2 + 1) * width / 8f
                    // Scale y from 10f to height - 10f
                    val y = height - 10f - ((t - minTemp) / tempRange * (height - 20f)).toFloat()
                    Offset(x, y)
                }

                // Draw Bezier smooth line
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val conX1 = (p0.x + p1.x) / 2f
                        val conY1 = p0.y
                        val conX2 = (p0.x + p1.x) / 2f
                        val conY2 = p1.y
                        cubicTo(conX1, conY1, conX2, conY2, p1.x, p1.y)
                    }
                }

                // Stroke with fine orange-yellow-blue gradient matching daily times
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFA726), // Morning
                            Color(0xFFFFEE58), // Day
                            Color(0xFFFB8C00), // Evening
                            Color(0xFF90CAF9)  // Night
                        )
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw glowing points
                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = 8.dp.toPx(),
                        center = point
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.TimelineHeaderItem(
    label: String,
    icon: ImageVector,
    iconColor: Color,
    temp: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = WeatherColors.TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = temp,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
