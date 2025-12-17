package com.hamkeitda.app.server.dto.facility.response

data class FacilityNearbyResponse(
    val id: Long,
    val name: String,
    val openHours: String?,
    val phone: String?,
    val address: String?,
    val description: String?,
    val lat: Double,
    val lng: Double,
    val imageUrl: String?,           // 대표 1장
    val imageUrls: List<String>      // 전체 URL
)
