package com.hamkeitda.app.server.mapper

import com.hamkeitda.app.server.dto.facility.response.CounselNotificationResponse
import com.hamkeitda.app.server.entity.facility.Counsel


fun Counsel.toNotificationDto(): CounselNotificationResponse =
    CounselNotificationResponse(
        id = this.id,
        facilityId = this.facility.id,
        facilityName = this.facility.name,
        applicantName = this.applicantName,
        applicantPhone = this.applicantPhone,
        answers = this.answers
    )