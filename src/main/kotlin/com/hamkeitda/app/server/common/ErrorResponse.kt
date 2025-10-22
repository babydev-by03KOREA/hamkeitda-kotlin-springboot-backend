package com.hamkeitda.app.server.common

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val status: Int,
    val code: String? = null,
    val message: String,
    val path: String? = null,
    val traceId: String? = null
)