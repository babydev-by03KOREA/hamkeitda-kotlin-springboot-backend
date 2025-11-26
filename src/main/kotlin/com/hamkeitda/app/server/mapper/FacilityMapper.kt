package com.hamkeitda.app.server.mapper

import com.hamkeitda.app.server.dto.facility.response.FacilityDetailResponse
import com.hamkeitda.app.server.dto.facility.response.FacilitySummaryResponse
import com.hamkeitda.app.server.entity.facility.BBS
import com.hamkeitda.app.server.entity.facility.Facility

fun Facility.toDetailDto(bbsList: List<BBS>): FacilityDetailResponse {
    return FacilityDetailResponse(
        id = this.id,
        name = this.name,
        address = this.address,
        phoneNumber = this.phoneNumber,
        openTime = this.openTime.toString(),
        closedTime = this.closedTime.toString(),
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,

        images = this.images.sortedBy { it.sortOrder }.map {
            FacilityDetailResponse.ImageDto(
                id = it.id,
                url = it.url,
                caption = it.caption,
                isPrimary = it.isPrimary,
                sortOrder = it.sortOrder
            )
        },

        necessaryDocuments = this.necessaryDocuments.sortedBy { it.sortOrder }.map {
            FacilityDetailResponse.DocumentDto(
                id = it.id,
                documentName = it.documentName,
                howToGet = it.howToGet,
                sortOrder = it.sortOrder
            )
        },

        programs = this.programs.sortedBy { it.sortOrder }.map {
            FacilityDetailResponse.ProgramDto(
                id = it.id,
                programName = it.programName,
                programDescription = it.programDescription,
                sortOrder = it.sortOrder
            )
        },

        fees = this.fees.sortedBy { it.sortOrder }.map {
            FacilityDetailResponse.FeeDto(
                id = it.id,
                title = it.title,
                feeText = it.feeText,
                sortOrder = it.sortOrder
            )
        },

        bbs = bbsList.map {
            FacilityDetailResponse.BbsSummaryDto(
                id = it.id,
                title = it.title,
                isPinned = it.isPinned,
                createdAt = it.createdAt.toString()
            )
        }
    )

}