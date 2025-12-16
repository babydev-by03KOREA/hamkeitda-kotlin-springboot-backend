package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.entity.facility.Program
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ProgramRepository : JpaRepository<Program, Long> {
    @Modifying
    @Query("delete from Program f where f.id = :id and f.facility.id = :facilityId")
    fun deleteByIdAndFacilityId(id: Long, facilityId: Long): Int
    fun findAllByFacilityIdOrderByIdDesc(facilityId: Long): List<Program>
}