package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.entity.facility.Counsel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CounselRepository : JpaRepository<Counsel, Long> {
    fun findByFacilityIdOrderByIdDesc(
        facilityId: Long,
        pageable: Pageable
    ): Page<Counsel>
}