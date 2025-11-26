package com.hamkeitda.app.server.repository.facility

import com.hamkeitda.app.server.entity.facility.NecessaryDocument
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NecessaryDocumentRepository : JpaRepository<NecessaryDocument, Long> {
    @Query(
        "select coalesce(max(nd.sortOrder), 0) " +
                "from NecessaryDocument nd " +
                "where nd.facility.id = :facilityId"
    )
    fun findMaxSortOrderByFacilityId(facilityId: Long): Int
}