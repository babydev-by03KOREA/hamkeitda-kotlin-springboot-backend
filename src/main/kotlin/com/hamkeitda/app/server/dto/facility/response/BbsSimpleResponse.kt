package com.hamkeitda.app.server.dto.facility.response

data class BbsSimpleResponse(
    val id: Long,
    val title: String,
    val content: String,
    val isPinned: Boolean
)