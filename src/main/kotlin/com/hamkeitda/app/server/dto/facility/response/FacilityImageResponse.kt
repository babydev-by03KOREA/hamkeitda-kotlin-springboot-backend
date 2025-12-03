package com.hamkeitda.app.server.dto.facility.response

class FacilityImageResponse(
    val id: Long,
    val url: String,
    val isPrimary: Boolean,
    val caption: String?,
    val sortOrder: Int
) {
}