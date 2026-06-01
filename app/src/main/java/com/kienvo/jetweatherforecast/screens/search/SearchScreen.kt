package com.kienvo.jetweatherforecast.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kienvo.jetweatherforecast.screens.components.WeatherAppBar
import com.kienvo.jetweatherforecast.screens.navigation.WeatherScreens
import com.kienvo.jetweatherforecast.ui.theme.GlassTokens
import com.kienvo.jetweatherforecast.ui.theme.WeatherColors

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val history by viewModel.searchHistory.collectAsStateWithLifecycle()
    val suggestions by viewModel.apiSuggestions.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current

    val onCitySelected = { cityName: String ->
        viewModel.saveSearchAndClear(cityName)
        keyboardController?.hide()
        navController.navigate(WeatherScreens.MainScreen.name + "/$cityName")
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
                    title = "Search City",
                    isMainScreen = false,
                    navController = navController
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Ô nhập liệu
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = { Text("Enter city name...", color = WeatherColors.TextTertiary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    trailingIcon = {
                        if (isSearching) CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (query.isNotBlank()) onCitySelected(query.trim())
                    }),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = GlassTokens.BackgroundMedium,
                        unfocusedContainerColor = GlassTokens.Background,
                        focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown kính mờ
                AnimatedVisibility(
                    visible = history.isNotEmpty() || suggestions.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -20 }),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassTokens.BackgroundMedium)
                    ) {
                        LazyColumn {
                            if (query.isNotBlank()) {
                                items(suggestions) { city ->
                                    CityListItem(
                                        title = "${city.name}, ${city.country}",
                                        icon = Icons.Default.LocationCity,
                                        onClick = { onCitySelected(city.name) }
                                    )
                                }
                            } else {
                                item {
                                    Text(
                                        text = "Recent Searches",
                                        color = WeatherColors.TextHint,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(16.dp, 8.dp)
                                    )
                                }
                                items(history) { historyItem ->
                                    CityListItem(
                                        title = historyItem.cityName,
                                        icon = Icons.Default.History,
                                        onClick = { onCitySelected(historyItem.cityName) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CityListItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = WeatherColors.TextTertiary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = Color.White, style = MaterialTheme.typography.bodyLarge)
    }
}