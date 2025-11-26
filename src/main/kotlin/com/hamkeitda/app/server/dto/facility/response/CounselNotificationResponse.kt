package com.hamkeitda.app.server.dto.facility.response

data class CounselNotificationResponse(
    val id: Long,
    val facilityId: Long,
    val facilityName: String,
    val applicantName: String,
    val applicantPhone: String?,
    val answers: String,
)
