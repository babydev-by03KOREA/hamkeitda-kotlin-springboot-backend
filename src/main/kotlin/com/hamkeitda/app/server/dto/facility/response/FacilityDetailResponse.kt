package com.hamkeitda.app.server.dto.facility.response

import java.math.BigDecimal

data class FacilityDetailResponse(
    val id: Long,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val openTime: String,
    val closedTime: String,
    val description: String,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,

    val images: List<ImageDto>,
    val necessaryDocuments: List<DocumentDto>,
    val programs: List<ProgramDto>,
    val fees: List<FeeDto>,

    val bbs: List<BbsSummaryDto>
) {
    data class ImageDto(
        val id: Long,
        val url: String,
        val caption: String?,
        val isPrimary: Boolean,
        val sortOrder: Int
    )

    data class DocumentDto(
        val id: Long,
        val documentName: String,
        val howToGet: String?,
        val sortOrder: Int
    )

    data class ProgramDto(
        val id: Long,
        val programName: String,
        val programDescription: String?,
        val sortOrder: Int
    )

    data class FeeDto(
        val id: Long,
        val title: String,
        val feeText: String,
        val sortOrder: Int
    )

    data class BbsSummaryDto(
        val id: Long,
        val title: String,
        val isPinned: Boolean,
        val createdAt: String
    )
}
