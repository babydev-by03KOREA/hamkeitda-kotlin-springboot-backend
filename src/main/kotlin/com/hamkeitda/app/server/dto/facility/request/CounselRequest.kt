package com.hamkeitda.app.server.dto.facility.request

data class CounselRequest (
    val answers: String,
    val applicantName: String,
    val applicantPhone: String?,
){
}