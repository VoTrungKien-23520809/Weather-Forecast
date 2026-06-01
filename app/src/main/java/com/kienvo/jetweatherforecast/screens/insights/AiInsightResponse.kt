package com.kienvo.jetweatherforecast.screens.insights

data class AiInsightResponse(
    val fashion_title: String? = "Fashion Advice",
    val fashion_recs: List<String>? = emptyList(),
    val fashion_reason: String? = "Appropriate for current conditions.",
    val health_title: String? = "Health & Activities",
    val health_recs: List<String>? = emptyList(),
    val health_reason: String? = "Stay active and stay safe."
)
