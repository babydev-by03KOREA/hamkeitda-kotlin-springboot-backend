package com.hamkeitda.app.server.dto.facility.response

import java.time.LocalDateTime

data class BbsResponse(
    val id: Long,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    val createdAt: LocalDateTime,
)
