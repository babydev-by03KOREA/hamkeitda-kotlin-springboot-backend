package com.hamkeitda.app.server.dto.auth

data class MeResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
    val facilityId: Long?
)
