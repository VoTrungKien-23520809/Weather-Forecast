package com.kienvo.jetweatherforecast.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.jetweatherforecast.data.local.Favorite
import com.kienvo.jetweatherforecast.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: FavoriteRepository
) : ViewModel() {

    // Dùng stateIn thay vì MutableStateFlow + collect thủ công
    // WhileSubscribed(5000) giữ flow active 5s sau khi UI detach (tránh restart khi xoay màn hình)
    val favList: StateFlow<List<Favorite>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertFavorite(favorite: Favorite) {
        viewModelScope.launch { repository.insertFavorite(favorite) }
    }

    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.launch { repository.deleteFavorite(favorite) }
    }
}