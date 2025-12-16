package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.entity.facility.Fee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FeeRepository : JpaRepository<Fee, Long> {
    @Modifying
    @Query("delete from Fee f where f.id = :id and f.facility.id = :facilityId")
    fun deleteByIdAndFacilityId(id: Long, facilityId: Long): Int
    fun findAllByFacilityIdOrderBySortOrderAsc(facilityId: Long): List<Fee>
}