package com.kienvo.jetweatherforecast.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.jetweatherforecast.data.local.SearchHistory
import com.kienvo.jetweatherforecast.data.local.SearchHistoryDao
import com.kienvo.jetweatherforecast.data.model.CitySuggestion
import com.kienvo.jetweatherforecast.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val searchDao: SearchHistoryDao // Nhúng DAO của Room vào đây
) : ViewModel() {

    // Từ khóa người dùng đang gõ
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Lịch sử tìm kiếm tự động cập nhật từ Database
    val searchHistory: StateFlow<List<SearchHistory>> = searchDao.getRecentSearches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Danh sách gợi ý từ API
    private val _apiSuggestions = MutableStateFlow<List<CitySuggestion>>(emptyList())
    val apiSuggestions: StateFlow<List<CitySuggestion>> = _apiSuggestions.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    init {
        // Lắng nghe người dùng gõ phím
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // KỸ THUẬT QUAN TRỌNG: Đợi 300ms sau khi ngừng gõ mới gọi API
                .collectLatest { query ->
                    if (query.trim().isNotEmpty()) {
                        _isSearching.value = true
                        _apiSuggestions.value = repository.getCitySuggestions(query)
                        _isSearching.value = false
                    } else {
                        _apiSuggestions.value = emptyList()
                    }
                }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    // Khi người dùng chốt tìm kiếm một thành phố
    fun saveSearchAndClear(cityName: String) {
        viewModelScope.launch {
            searchDao.insertSearch(SearchHistory(cityName = cityName))
            _searchQuery.value = ""
        }
    }
}