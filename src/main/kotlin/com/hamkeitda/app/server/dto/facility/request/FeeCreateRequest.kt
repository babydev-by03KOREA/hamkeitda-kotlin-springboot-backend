package com.hamkeitda.app.server.dto.facility.request

data class FeeCreateRequest(
    val title: String,
    val feeText: String,          // "무료", "문의", "변동" 등
    val sortOrder: Int? = null
)
