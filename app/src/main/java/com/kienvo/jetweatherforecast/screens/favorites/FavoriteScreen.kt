package com.kienvo.jetweatherforecast.screens.favorites

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import com.kienvo.jetweatherforecast.utils.bounceClick
import kotlinx.coroutines.delay
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kienvo.jetweatherforecast.data.local.Favorite
import com.kienvo.jetweatherforecast.screens.components.WeatherAppBar
import com.kienvo.jetweatherforecast.screens.navigation.WeatherScreens
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors

@Composable
fun FavoriteScreen(navController: NavController, favoriteViewModel: FavoriteViewModel = hiltViewModel()) {
    val favList by favoriteViewModel.favList.collectAsStateWithLifecycle()

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
                    title = "Favorite Cities",
                    isMainScreen = false,
                    navController = navController
                )
            }
        ) { paddingValues ->
            if (favList.isEmpty()) {
                // ── Empty State ──────────────────────────
                EmptyFavoritesState(modifier = Modifier.padding(paddingValues))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = favList,
                        key = { _, item -> item.city }
                    ) { index, favorite ->
                        var isItemVisible by remember(favorite.city) { mutableStateOf(false) }
                        LaunchedEffect(favorite.city) {
                            delay(index * 60L) // Staggered loading delay
                            isItemVisible = true
                        }
                        AnimatedVisibility(
                            visible = isItemVisible,
                            enter = fadeIn(animationSpec = tween(400)) +
                                    slideInVertically(animationSpec = tween(400), initialOffsetY = { 30 }),
                            exit = fadeOut(animationSpec = tween(200))
                        ) {
                            FavoriteCityRow(
                                favorite = favorite,
                                onCityClick = {
                                    // Navigate & clear back to avoid stacking
                                    navController.navigate(WeatherScreens.MainScreen.name + "/${favorite.city}") {
                                        popUpTo(WeatherScreens.MainScreen.name + "/{city}") {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                onDeleteClick = {
                                    favoriteViewModel.deleteFavorite(favorite)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassTokens.Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "No Favorites",
                    tint = WeatherColors.TextTertiary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No favorites yet",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the heart icon on the main screen to save your favorite cities here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.TextTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FavoriteCityRow(
    favorite: Favorite,
    onCityClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Delete button scale animation
    var isDeleting by remember { mutableStateOf(false) }
    val deleteScale by animateFloatAsState(
        targetValue = if (isDeleting) 0.7f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "deleteScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick { onCityClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassTokens.Background),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, GlassTokens.Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = favorite.city,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = favorite.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WeatherColors.TextTertiary
                )
            }
            IconButton(
                onClick = {
                    isDeleting = true
                    onDeleteClick()
                },
                modifier = Modifier.scale(deleteScale)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Favorite",
                    tint = WeatherColors.DeleteRed
                )
            }
        }
    }
}