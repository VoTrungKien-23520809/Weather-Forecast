package com.kienvo.jetweatherforecast.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.jetweatherforecast.data.local.Unit
import com.kienvo.jetweatherforecast.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: SettingsRepository) : ViewModel() {

    // Dùng stateIn thay vì MutableStateFlow + collect thủ công
    val unitList: StateFlow<List<Unit>> = repository.getUnits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertUnit(unit: Unit) {
        viewModelScope.launch { repository.insertUnit(unit) }
    }

    fun updateUnit(unit: Unit) {
        viewModelScope.launch { repository.updateUnit(unit) }
    }

    fun deleteAllUnits() {
        viewModelScope.launch { repository.deleteAllUnits() }
    }

    fun updateSettingsUnit(unit: Unit) {
        viewModelScope.launch {
            repository.deleteAllUnits()
            repository.insertUnit(unit)
        }
    }
}