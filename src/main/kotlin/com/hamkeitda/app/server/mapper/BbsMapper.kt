package com.hamkeitda.app.server.mapper

import com.hamkeitda.app.server.dto.facility.response.BbsResponse
import com.hamkeitda.app.server.dto.facility.response.BbsSimpleResponse
import com.hamkeitda.app.server.entity.facility.BBS

fun BBS.toDto(): BbsResponse =
    BbsResponse(
        id = this.id,
        title = this.title,
        content = this.content,
        isPinned = this.isPinned,
        createdAt = this.createdAt
    )

fun BBS.toSimpleDto() = BbsSimpleResponse(
    id = id,
    title = title,
    content = content,
    isPinned = isPinned
)