package com.kienvo.jetweatherforecast.screens.components

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kienvo.jetweatherforecast.data.local.Favorite
import com.kienvo.jetweatherforecast.screens.favorites.FavoriteViewModel
import com.kienvo.jetweatherforecast.screens.navigation.WeatherScreens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppBar(
    title: String,
    isMainScreen: Boolean = true,
    navController: NavController,
    snackbarHostState: SnackbarHostState? = null,
    onSearchClicked: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        navigationIcon = {
            if (!isMainScreen) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back Icon",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (isMainScreen) {
                val favViewModel = hiltViewModel<FavoriteViewModel>()
                val favList by favViewModel.favList.collectAsStateWithLifecycle()
                val context = LocalContext.current

                val cityName = title.split(",").firstOrNull()?.trim() ?: ""
                val country = title.split(",").lastOrNull()?.trim() ?: ""
                val isAlreadyFavorite = favList.any { it.city == cityName }

                // ── Heart Animation ─────────────────────
                val heartScale = remember { Animatable(1f) }
                val heartColor by animateColorAsState(
                    targetValue = if (isAlreadyFavorite) WeatherColors.FavoriteRed else Color.White,
                    animationSpec = tween(400),
                    label = "heartColor"
                )

                IconButton(onClick = {
                    if (isAlreadyFavorite) {
                        favViewModel.deleteFavorite(Favorite(city = cityName, country = country))
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar("Removed from Favorites")
                        }                    } else {
                        favViewModel.insertFavorite(Favorite(city = cityName, country = country))
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar("Added to Favorites")
                        }
                    }
                }) {
                    // Kích hoạt animation scale khi trạng thái thay đổi
                    LaunchedEffect(isAlreadyFavorite) {
                        heartScale.animateTo(
                            1.3f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )
                        heartScale.animateTo(1f)
                    }

                    Icon(
                        imageVector = if (isAlreadyFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite Icon",
                        tint = heartColor,
                        modifier = Modifier.scale(heartScale.value)
                    )
                }

                IconButton(onClick = { onSearchClicked.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White
                    )
                }

                ShowSettingDropDownMenu(
                    showDialog = showDialog,
                    navController = navController,
                    cityName = cityName
                ) {
                    showDialog = false
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun ShowSettingDropDownMenu(
    showDialog: Boolean,
    navController: NavController,
    cityName: String,
    onDismiss: () -> Unit
) {
    // Menu items with icons
    val menuItems = listOf(
        Triple("Favorites", Icons.Outlined.Bookmarks, WeatherScreens.FavoriteScreen.name),
        Triple("Insights", Icons.Outlined.Lightbulb, "${WeatherScreens.WeatherInsightsScreen.name}/$cityName"),
        Triple("About", Icons.Outlined.Info, WeatherScreens.AboutScreen.name),
        Triple("Settings", Icons.Outlined.Settings, WeatherScreens.SettingsScreen.name),
    )

    DropdownMenu(
        expanded = showDialog,
        onDismissRequest = { onDismiss() },
        modifier = Modifier
            .background(WeatherColors.MenuBackground)
            .width(180.dp)
    ) {
        menuItems.forEach { (text, icon, route) ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = text,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    onDismiss()
                    navController.navigate(route)
                }
            )
        }
    }
}