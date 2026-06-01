package com.kienvo.jetweatherforecast.data.model

data class CitySuggestion(
    val name: String,
    val country: String,
    val state: String? = null
)