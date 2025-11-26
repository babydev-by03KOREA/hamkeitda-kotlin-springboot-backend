package com.hamkeitda.app.server.dto.facility.request

data class NecessaryDocumentCreateRequest(
    val documentName: String,
    val howToGet: String? = null,
    val sortOrder: Int? = null // 안 보내면 자동으로 맨 뒤에 붙게 처리
)