package com.hamkeitda.app.server.dto.facility

data class FacilitySummary(
    val id: Long,
    val name: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?
)
