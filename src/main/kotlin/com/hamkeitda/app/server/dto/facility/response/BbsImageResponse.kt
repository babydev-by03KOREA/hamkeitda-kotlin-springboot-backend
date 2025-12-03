package com.hamkeitda.app.server.dto.facility.response

class BbsImageResponse(
    val id: Long,
    val url: String,
    val isPrimary: Boolean,
    val caption: String?,
    val sortOrder: Int
) {
}