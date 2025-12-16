package com.hamkeitda.app.server.dto.facility.response

data class FeeResponse(
    val id: Long,
    val title: String,
    val feeText: String,
    val sortOrder: Int
)
