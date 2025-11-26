package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.dto.facility.response.FacilitySummaryResponse
import com.hamkeitda.app.server.entity.facility.Facility
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FacilityRepository: JpaRepository<Facility, Long> {

    /** 이름 또는 주소로 검색 */
    @Query("""
        SELECT f
        FROM Facility f
        WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(f.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    fun searchByKeyword(keyword: String): List<Facility>

    // 상세 조회용 (이미지/문서/프로그램 fetch join 할 수도 있음)
    @Query("""
        select f
        from Facility f
        where f.id = :id
    """)
    fun findDetailById(id: Long): Facility?

    // 주변 시설 목록 (간단 버전: lat/lng와 거리 계산은 DB function 활용)
    @Query(
        """
        select f
        from Facility f
        where function('earth_distance',
                      function('ll_to_earth', f.latitude, f.longitude),
                      function('ll_to_earth', :lat, :lng)
             ) <= :radius
        """
    )
    fun findNearby(lat: Double, lng: Double, radius: Double): List<Facility>

    // 주변 시설 좌표만
    @Query(
        """
        select FacilityPointResponse(
            f.id, f.name, f.latitude, f.longitude
        )
        from Facility f
        where function('earth_distance',
                      function('ll_to_earth', f.latitude, f.longitude),
                      function('ll_to_earth', :lat, :lng)
             ) <= :radius
        """
    )
    fun findNearbyPoints(lat: Double, lng: Double, radius: Double): List<Any> // 실제 DTO 타입으로 변경

    // 전체 요약
    @Query(
        """
        select FacilitySummaryResponse(
            f.id, f.name, f.address, f.latitude, f.longitude
        )
        from Facility f
        """
    )
    fun findAllSummary(): List<FacilitySummaryResponse>

    // 검색
    @Query(
        """
        select FacilitySummaryResponse(
            f.id, f.name, f.address, f.latitude, f.longitude
        )
        from Facility f
        where lower(f.name) like lower(concat('%', :keyword, '%'))
           or lower(f.address) like lower(concat('%', :keyword, '%'))
        """
    )
    fun search(keyword: String, pageable: Pageable): Page<FacilitySummaryResponse>
}