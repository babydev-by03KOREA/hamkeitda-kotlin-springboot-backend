package com.hamkeitda.app.server.controller

import com.hamkeitda.app.server.dto.facility.request.CounselRequest
import com.hamkeitda.app.server.service.FacilityService
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/facility")
class FacilityController(
    private val facilityService: FacilityService,
) {
    private val log = LoggerFactory.getLogger(FacilityController::class.java)

    // 시설 기본 정보 불러오기
    @GetMapping("/{id}")
    fun getDetail(@PathVariable id: Long) =
        facilityService.getDetail(id)

    // 게시물 관리 - 등록된 게시물 불러오기
    @GetMapping("/{id}/bbs")
    fun getBbs(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ) = facilityService.getBbs(id, page, size)

    // 지도 핀(근처) 시설 불러오기
    @GetMapping("/nearby")
    fun nearby(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(defaultValue = "1000") radius: Int,
    ) = facilityService.getNearby(lat, lng, radius)

    // 근처 시설 위치 불러오기
    @GetMapping("/nearby/points")
    fun nearbyPoints(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(defaultValue = "1000") radius: Int,
    ) = facilityService.getNearbyPoints(lat, lng, radius)

    // 전체 시설 불러오기
    @GetMapping("/list")
    fun list() = facilityService.getAll()

    // 전체 시설 검색
    @GetMapping("/search")
    fun search(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ) = facilityService.search(keyword, page, size)

    // 상담 신청하기
    @PostMapping("/{id}/counsel")
    @PreAuthorize("isAuthenticated()")
    fun submitCounsel(
        @PathVariable id: Long,
        @RequestBody req: CounselRequest
    ) = facilityService.submitCounsel(id, req)
}