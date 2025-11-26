package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.entity.facility.BBS
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BbsRepository : JpaRepository<BBS, Long> {
    fun findAllByFacilityId(facilityId: Long, pageable: Pageable): Page<BBS>
    fun findAllByFacilityIdOrderByIsPinnedDescCreatedAtDesc(
        facilityId: Long,
        pageable: Pageable
    ): Page<BBS>
}