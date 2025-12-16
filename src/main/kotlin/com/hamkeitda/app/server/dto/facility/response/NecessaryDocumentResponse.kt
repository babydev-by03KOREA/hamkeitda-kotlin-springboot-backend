package com.hamkeitda.app.server.dto.facility.response

data class NecessaryDocumentResponse(
    val id: Long,
    val name: String,
    val howToGet: String?,
)
