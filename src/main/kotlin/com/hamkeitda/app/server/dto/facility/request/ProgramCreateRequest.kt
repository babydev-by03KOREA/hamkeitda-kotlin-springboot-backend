package com.hamkeitda.app.server.dto.facility.request

data class ProgramCreateRequest(
    val programName: String,
    val programDescription: String?,
    val sortOrder: Int? = null
)
