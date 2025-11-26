package com.hamkeitda.app.server.service

import com.hamkeitda.app.server.common.exception.ApiException
import com.hamkeitda.app.server.dto.facility.request.CounselRequest
import com.hamkeitda.app.server.dto.facility.response.BbsResponse
import com.hamkeitda.app.server.dto.facility.response.FacilityDetailResponse
import com.hamkeitda.app.server.dto.facility.response.FacilitySummaryResponse
import com.hamkeitda.app.server.entity.facility.Counsel
import com.hamkeitda.app.server.mapper.toDetailDto
import com.hamkeitda.app.server.mapper.toDto
import com.hamkeitda.app.server.repository.facility.BbsRepository
import com.hamkeitda.app.server.repository.facility.CounselRepository
import com.hamkeitda.app.server.repository.facility.FacilityRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
@Transactional
class FacilityService(
    private val facilityRepo: FacilityRepository,
    private val bbsRepo: BbsRepository,
    private val counselRepo: CounselRepository,
) {
    /**
     * 시설 상세 조회 + 상단에 보여줄 공지/BBS 목록 일부 같이 가져오기
     */
    fun getDetail(id: Long, bbsPage: Int = 0, bbsSize: Int = 5): FacilityDetailResponse {
        val fac = facilityRepo.findDetailById(id)
            ?: throw ApiException(HttpStatus.NOT_FOUND, "시설 없음")

        val pageable = PageRequest.of(bbsPage, bbsSize)
        val bbsList = bbsRepo.findAllByFacilityId(id, pageable).content

        return fac.toDetailDto(bbsList)
    }

    /**
     * 게시판 페이징 조회
     */
    fun getBbs(id: Long, page: Int, size: Int): Page<BbsResponse> =
        bbsRepo.findAllByFacilityId(id, PageRequest.of(page, size))
            .map { it.toDto() } // 여기서 BbsResponse로 매핑

    /**
     * 근처 시설(전체 정보 or 최소 요약) 조회
     */
    fun getNearby(lat: Double, lng: Double, radiusMeters: Int) =
        facilityRepo.findNearby(lat, lng, radiusMeters.toDouble())

    fun getNearbyPoints(lat: Double, lng: Double, radiusMeters: Int) =
        facilityRepo.findNearbyPoints(lat, lng, radiusMeters.toDouble())

    /**
     * 전체 시설 요약 목록
     */
    fun getAll(): List<FacilitySummaryResponse> =
        facilityRepo.findAllSummary()

    /**
     * 시설 검색
     */
    fun search(keyword: String, page: Int, size: Int) =
        facilityRepo.search(keyword, PageRequest.of(page, size))

    /**
     * 상담 신청
     */
    fun submitCounsel(id: Long, req: CounselRequest): Long {
        val fac = facilityRepo.findById(id)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "시설 없음") }

        val saved = counselRepo.save(
            Counsel(
                facility = fac,
                answers = req.answers,
                applicantName = req.applicantName,
                applicantPhone = req.applicantPhone
            )
        )
        return saved.id
    }
}