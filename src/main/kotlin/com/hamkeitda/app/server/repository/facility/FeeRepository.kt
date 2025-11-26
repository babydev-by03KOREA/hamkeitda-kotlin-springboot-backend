package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.entity.facility.Fee
import org.springframework.data.jpa.repository.JpaRepository

interface FeeRepository : JpaRepository<Fee, Long> {
    fun deleteByIdAndFacilityId(id: Long, facilityId: Long): Long
}