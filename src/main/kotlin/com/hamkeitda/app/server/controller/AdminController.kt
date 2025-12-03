package com.hamkeitda.app.server.controller

import com.hamkeitda.app.server.dto.facility.request.BbsCreateRequest
import com.hamkeitda.app.server.dto.facility.request.FacilitySaveRequest
import com.hamkeitda.app.server.dto.facility.request.FeeCreateRequest
import com.hamkeitda.app.server.dto.facility.request.NecessaryDocumentCreateRequest
import com.hamkeitda.app.server.dto.facility.request.ProgramCreateRequest
import com.hamkeitda.app.server.dto.facility.response.BbsImageResponse
import com.hamkeitda.app.server.dto.facility.response.CounselNotificationResponse
import com.hamkeitda.app.server.dto.facility.response.FacilityImageResponse
import com.hamkeitda.app.server.dto.facility.response.IdResponse
import com.hamkeitda.app.server.service.AdminService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val adminService: AdminService,
) {
    /**
     * 시설 기본 정보 저장하기
     * POST /api/admin/facility/basic
     */
    @PostMapping("/basic")
    fun saveFacilityBasic(
        @RequestBody req: FacilitySaveRequest
    ): IdResponse =
        adminService.saveFacilityBasic(req)

    /**
     * 시설 기본 정보 수정하기
     * PUT /api/admin/facility/{facilityId}/basic
     */
    @PutMapping("/{facilityId}/basic")
    fun updateFacilityBasic(
        @PathVariable facilityId: Long,
        @RequestBody req: FacilitySaveRequest
    ): IdResponse =
        adminService.updateFacilityBasic(facilityId, req)

    /**
     * 필요 서류 관리 - 등록 (단일)
     * POST /api/admin/facility/{facilityId}/documents
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{facilityId}/documents")
    fun addNecessaryDocument(
        @PathVariable facilityId: Long,
        @RequestBody req: NecessaryDocumentCreateRequest
    ): ResponseEntity<Map<String, Long>> {
        val id = adminService.addNecessaryDocument(facilityId, req)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapOf("id" to id))
    }

    /**
     * 필요 서류 관리 - 개별 삭제
     * DELETE /api/admin/facility/{facilityId}/documents/{documentId}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{facilityId}/documents/{documentId}")
    fun deleteNecessaryDocument(
        @PathVariable facilityId: Long,
        @PathVariable documentId: Long
    ): ResponseEntity<Void> {
        adminService.deleteNecessaryDocument(facilityId, documentId)
        return ResponseEntity.noContent().build()
    }

    /**
     * 프로그램 관리 - 추가하기
     * POST /api/admin/facility/{facilityId}/programs
     */
    @PostMapping("/{facilityId}/programs")
    fun addProgram(
        @PathVariable facilityId: Long,
        @RequestBody req: ProgramCreateRequest
    ): IdResponse =
        adminService.addProgram(facilityId, req)

    /**
     * 프로그램 관리 - 삭제하기
     * DELETE /api/admin/facility/{facilityId}/programs/{programId}
     */
    @DeleteMapping("/{facilityId}/programs/{programId}")
    fun deleteProgram(
        @PathVariable facilityId: Long,
        @PathVariable programId: Long
    ) {
        adminService.deleteProgram(facilityId, programId)
    }

    /**
     * 이용료 관리 - 추가하기
     * POST /api/admin/facility/{facilityId}/fees
     */
    @PostMapping("/{facilityId}/fees")
    fun addFee(
        @PathVariable facilityId: Long,
        @RequestBody req: FeeCreateRequest
    ): IdResponse =
        adminService.addFee(facilityId, req)

    /**
     * 이용료 관리 - 삭제하기
     * DELETE /api/admin/facility/{facilityId}/fees/{feeId}
     */
    @DeleteMapping("/{facilityId}/fees/{feeId}")
    fun deleteFee(
        @PathVariable facilityId: Long,
        @PathVariable feeId: Long
    ) {
        adminService.deleteFee(facilityId, feeId)
    }

    /**
     * 게시물 관리 - 게시물 등록하기
     * POST /api/admin/facility/{facilityId}/bbs
     */
    @PostMapping("/{facilityId}/bbs")
    fun createBbs(
        @PathVariable facilityId: Long,
        @RequestBody req: BbsCreateRequest
    ): IdResponse =
        adminService.createBbs(facilityId, req)

    /**
     * 상담 신청 알림 불러오기
     * GET /api/admin/facility/{facilityId}/counsels?page=0&size=20
     */
    @GetMapping("/{facilityId}/counsels")
    fun getCounselNotifications(
        @PathVariable facilityId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Page<CounselNotificationResponse> =
        adminService.getCounselNotifications(facilityId, page, size)

    /**
     * 시설 이미지 업로드
     * POST /api/admin/facility/{facilityId}/images
     * multipart/form-data
     *  - file: 이미지 파일
     *  - isPrimary: 대표 이미지 여부 (optional, default=false)
     *  - caption: 캡션 (optional)
     */
    @PostMapping("/{facilityId}/images", consumes = ["multipart/form-data"])
    fun uploadFacilityImage(
        @PathVariable facilityId: Long,
        @RequestPart("file") file: MultipartFile,
        @RequestParam(required = false, defaultValue = "false") isPrimary: Boolean,
        @RequestParam(required = false) caption: String?
    ): FacilityImageResponse =
        adminService.uploadFacilityImage(facilityId, file, isPrimary, caption)

    /**
     * 게시물 이미지 업로드
     * POST /api/admin/facility/{facilityId}/bbs/{bbsId}/images
     */
    @PostMapping("/{facilityId}/bbs/{bbsId}/images", consumes = ["multipart/form-data"])
    fun uploadBbsImage(
        @PathVariable facilityId: Long,          // 혹시 추후 권한 체크 등을 위해 함께 받기
        @PathVariable bbsId: Long,
        @RequestPart("file") file: MultipartFile,
        @RequestParam(required = false, defaultValue = "false") isPrimary: Boolean,
        @RequestParam(required = false) caption: String?
    ): BbsImageResponse =
        adminService.uploadBbsImage(facilityId, bbsId, file, isPrimary, caption)
}