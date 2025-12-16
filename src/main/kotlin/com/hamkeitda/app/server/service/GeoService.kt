package com.hamkeitda.app.server.service

import java.math.BigDecimal

interface GeoService {
    fun geocode(address: String): GeoPoint
}

data class GeoPoint(
    val lat: BigDecimal,
    val lng: BigDecimal
)