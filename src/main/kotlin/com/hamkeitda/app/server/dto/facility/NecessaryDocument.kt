package com.hamkeitda.app.server.dto.facility

data class NecessaryDocumentCreate(
    val documentName: String,
    val howToGet: String?
)