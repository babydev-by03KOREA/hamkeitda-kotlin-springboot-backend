package com.hamkeitda.app.server.dto.facility

data class NaverAddressResponse(
    val addresses: List<NaverAddressDocument>
)

data class NaverAddressDocument(
    val x: String, // longitude
    val y: String  // latitude
)