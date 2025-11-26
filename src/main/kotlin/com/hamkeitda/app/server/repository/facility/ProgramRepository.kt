package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.entity.facility.Program
import org.springframework.data.jpa.repository.JpaRepository

interface ProgramRepository : JpaRepository<Program, Long> {
    fun deleteByIdAndFacilityId(id: Long, facilityId: Long): Long
}