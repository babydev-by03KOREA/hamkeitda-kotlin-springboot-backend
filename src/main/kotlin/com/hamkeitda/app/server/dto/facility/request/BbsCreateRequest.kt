package com.hamkeitda.app.server.dto.facility.request

data class BbsCreateRequest(
    val title: String,
    val content: String,
    val isPinned: Boolean = false
)