package com.hamkeitda.app.server.dto.facility.request

import java.time.LocalTime

data class FacilitySaveRequest(
    val id: Long?,                  // null이면 신규 생성, 아니면 (나중에) 수정용으로 확장 가능
    val name: String,
    val openTime: LocalTime,
    val closedTime: LocalTime,
    val phoneNumber: String,
    val address: String,
    val description: String,
)
