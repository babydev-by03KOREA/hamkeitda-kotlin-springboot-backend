package com.hamkeitda.app.server.dto.facility.response

data class FacilityBasicResponse(
    val name: String,
    val openHours: String,   // "09:00 - 18:00"
    val phone: String,
    val address: String,
    val description: String,
    val imageUrls: List<String>
)
