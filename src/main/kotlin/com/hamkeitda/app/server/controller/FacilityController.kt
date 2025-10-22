package com.hamkeitda.app.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/facility")
class FacilityController {
    @GetMapping("/dashboard")
    fun dashboard(): String = "시설 관리자 전용 대시보드"
}