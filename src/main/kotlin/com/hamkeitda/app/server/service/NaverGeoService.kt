package com.hamkeitda.app.server.service

import com.hamkeitda.app.server.common.exception.ApiException
import com.hamkeitda.app.server.dto.facility.NaverAddressResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@Service
class NaverGeoService(
    @Value("\${naver.client-id}") private val clientId: String,
    @Value("\${naver.client-secret}") private val clientSecret: String,
    private val restTemplate: RestTemplate = RestTemplate()
) : GeoService {
    override fun geocode(address: String): GeoPoint {
        println("ID: $clientId")
        println("Secret: $clientSecret")
        val url = UriComponentsBuilder
            .fromHttpUrl("https://maps.apigw.ntruss.com/map-geocode/v2/geocode")
            .queryParam("query", address)
            .encode() // 네이버도 한글 인코딩이 필수입니다.
            .build()
            .toUri()

        val headers = HttpHeaders().apply {
            set("X-NCP-APIGW-API-KEY-ID", clientId)
            set("X-NCP-APIGW-API-KEY", clientSecret)
        }

        val entity = HttpEntity<Void>(headers)

        // 응답 DTO는 네이버의 JSON 구조(addresses 리스트 등)에 맞춰 새로 만드셔야 합니다.
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            NaverAddressResponse::class.java
        )

        val addr = response.body?.addresses?.firstOrNull()
            ?: throw ApiException(HttpStatus.BAD_REQUEST, "주소를 찾을 수 없습니다.")

        return GeoPoint(
            lat = addr.y.toBigDecimal(),
            lng = addr.x.toBigDecimal()
        )
    }
}