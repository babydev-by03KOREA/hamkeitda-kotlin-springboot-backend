package com.hamkeitda.app.server.dto.facility.response

import java.time.LocalTime

data class FacilitySummaryResponse(
    val id: Long,
    val name: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val mainImageUrl: String?,        // 시설 대표 이미지 (없으면 null)
    val openTime: LocalTime?,
    val closedTime: LocalTime?,
)
