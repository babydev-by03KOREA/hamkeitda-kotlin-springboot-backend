package com.hamkeitda.app.server.service

import com.hamkeitda.app.server.common.exception.ApiException
import com.hamkeitda.app.server.dto.facility.request.BbsCreateRequest
import com.hamkeitda.app.server.dto.facility.request.FacilitySaveRequest
import com.hamkeitda.app.server.dto.facility.request.FeeCreateRequest
import com.hamkeitda.app.server.dto.facility.request.NecessaryDocumentCreateRequest
import com.hamkeitda.app.server.dto.facility.request.ProgramCreateRequest
import com.hamkeitda.app.server.dto.facility.response.BbsImageResponse
import com.hamkeitda.app.server.dto.facility.response.CounselNotificationResponse
import com.hamkeitda.app.server.dto.facility.response.FacilityImageResponse
import com.hamkeitda.app.server.dto.facility.response.IdResponse
import com.hamkeitda.app.server.entity.facility.BBS
import com.hamkeitda.app.server.entity.facility.Facility
import com.hamkeitda.app.server.entity.facility.Fee
import com.hamkeitda.app.server.entity.facility.NecessaryDocument
import com.hamkeitda.app.server.entity.facility.Program
import com.hamkeitda.app.server.mapper.toNotificationDto
import com.hamkeitda.app.server.repository.facility.BbsRepository
import com.hamkeitda.app.server.repository.facility.CounselRepository
import com.hamkeitda.app.server.repository.facility.FacilityRepository
import com.hamkeitda.app.server.repository.facility.FeeRepository
import com.hamkeitda.app.server.repository.facility.NecessaryDocumentRepository
import com.hamkeitda.app.server.repository.facility.ProgramRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class AdminService(
    private val facilityRepo: FacilityRepository,
    private val necessaryDocRepo: NecessaryDocumentRepository,
    private val programRepo: ProgramRepository,
    private val feeRepo: FeeRepository,
    private val bbsRepo: BbsRepository,
    private val counselRepo: CounselRepository,
    private val fileService: FileService,
) {
    /**
     * 시설 기본 정보 저장 (일단은 생성 전용으로 생각)
     * 수정까지 하고 싶으면 Facility 엔티티의 필드를 var로 바꾸거나 update 메서드를 추가해야 함.
     */
    fun saveFacilityBasic(req: FacilitySaveRequest): IdResponse {
        val facility = Facility(
            name = req.name,
            openTime = req.openTime,
            closedTime = req.closedTime,
            phoneNumber = req.phoneNumber,
            address = req.address,
            description = req.description
        )

        val saved = facilityRepo.save(facility)
        return IdResponse(saved.id)
    }

    /**
     * 시설 기본 정보 수정
     */
    fun updateFacilityBasic(facilityId: Long, req: FacilitySaveRequest): IdResponse {
        val fac = facilityRepo.findById(facilityId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "시설 없음") }

        fac.updateBasicInfo(req)   // 영속 엔티티 수정 → @Transactional이라 flush 시 자동 반영

        return IdResponse(fac.id)
    }

    /**
     * 필요 서류 등록 (단일)
     */
    fun addNecessaryDocument(
        facilityId: Long,
        req: NecessaryDocumentCreateRequest
    ): Long {
        val facility = facilityRepo.findById(facilityId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "시설 없음") }

        val nextSortOrder =
            req.sortOrder ?: (necessaryDocRepo.findMaxSortOrderByFacilityId(facilityId) + 1)

        val entity = NecessaryDocument(
            facility = facility,
            documentName = req.documentName,
            howToGet = req.howToGet,
            sortOrder = nextSortOrder
        )

        return necessaryDocRepo.save(entity).id
    }

    /**
     * 필요 서류 개별 삭제
     */
    fun deleteNecessaryDocument(
        facilityId: Long,
        documentId: Long
    ) {
        val doc = necessaryDocRepo.findById(documentId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "필요 서류 없음") }

        if (doc.facility.id != facilityId) {
            // URL로 넘어온 facilityId와 소유 시설이 다르면 잘못된 요청
            throw ApiException(HttpStatus.BAD_REQUEST, "시설에 속하지 않은 서류입니다.")
        }

        necessaryDocRepo.delete(doc)
    }

    /**
     * 프로그램 추가
     */
    fun addProgram(facilityId: Long, req: ProgramCreateRequest): IdResponse {
        val facility = facilityRepo.findById(facilityId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "시설 없음") }

        val order = req.sortOrder ?: 0

        val program = Program(
            facility = facility,
            programName = req.programName,
            programDescription = req.programDescription,
            sortOrder = order
        )

        val saved = programRepo.save(program)
        return IdResponse(saved.id)
    }

    /**
     * 프로그램 삭제
     */
    fun deleteProgram(facilityId: Long, programId: Long) {
        val deleted = programRepo.deleteByIdAndFacilityId(programId, facilityId)
        if (deleted == 0L) {
            throw ApiException(HttpStatus.NOT_FOUND, "해당 프로그램이 존재하지 않거나 시설에 속하지 않습니다.")
        }
    }

    /**
     * 이용료 추가
     */
    fun addFee(facilityId: Long, req: FeeCreateRequest): IdResponse {
        val facility = facilityRepo.findById(facilityId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "시설 없음") }

        val order = req.sortOrder ?: 0

        val fee = Fee(
            facility = facility,
            title = req.title,
            feeText = req.feeText,
            sortOrder = order
        )

        val saved = feeRepo.save(fee)
        return IdResponse(saved.id)
    }

    /**
     * 이용료 삭제
     */
    fun deleteFee(facilityId: Long, feeId: Long) {
        val deleted = feeRepo.deleteByIdAndFacilityId(feeId, facilityId)
        if (deleted == 0L) {
            throw ApiException(HttpStatus.NOT_FOUND, "해당 이용료가 존재하지 않거나 시설에 속하지 않습니다.")
        }
    }

    /**
     * 게시물 등록
     */
    fun createBbs(facilityId: Long, req: BbsCreateRequest): IdResponse {
        val facility = facilityRepo.findById(facilityId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "시설 없음") }

        val bbs = BBS(
            facility = facility,
            title = req.title,
            content = req.content,
            isPinned = req.isPinned
        )

        val saved = bbsRepo.save(bbs)
        return IdResponse(saved.id)
    }

    /**
     * 상담 신청 알림 조회 (기관별)
     */
    fun getCounselNotifications(
        facilityId: Long,
        page: Int,
        size: Int
    ): Page<CounselNotificationResponse> {
        val pageable = PageRequest.of(page, size)
        return counselRepo.findByFacilityIdOrderByIdDesc(facilityId, pageable)
            .map { it.toNotificationDto() }
    }


    /**
     * 시설 이미지 업로드
     */
    fun uploadFacilityImage(
        facilityId: Long,
        file: MultipartFile,
        isPrimary: Boolean,
        caption: String?
    ): FacilityImageResponse {
        val facility = facilityRepo.findById(facilityId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "시설 없음") }

        // 파일 경로: facility/{facilityId}/images/...
        val dir = fileService.buildFilePath("facility", facilityId, "images")
        val url = fileService.uploadFile(file, dir)

        // 연관 엔티티에 추가
        facility.addImage(
            url = url,
            isPrimary = isPrimary,
            caption = caption,
            sortOrder = null      // null이면 내부에서 자동 sortOrder 부여
        )

        // 영속 엔티티라 별도 save() 필요 X (@Transactional)
        val savedImage = facility.images.maxBy { it.id }   // 방금 추가된 것

        return FacilityImageResponse(
            id = savedImage.id,
            url = savedImage.url,
            isPrimary = savedImage.isPrimary,
            caption = savedImage.caption,
            sortOrder = savedImage.sortOrder
        )
    }

    @Transactional
    fun uploadBbsImage(
        facilityId: Long,
        bbsId: Long,
        file: MultipartFile,
        isPrimary: Boolean,
        caption: String?
    ): BbsImageResponse {
        val bbs = bbsRepo.findById(bbsId)
            .orElseThrow { ApiException(HttpStatus.NOT_FOUND, "게시물 없음") }

        // 안전하게: 해당 BBS가 해당 facility에 속하는지 검증
        if (bbs.facility.id != facilityId) {
            throw ApiException(HttpStatus.BAD_REQUEST, "시설/게시물 정보가 일치하지 않습니다.")
        }

        // 파일 경로: facility/{facilityId}/bbs/{bbsId}/images/...
        val dir = fileService.buildFilePath("facility", facilityId, "bbs/$bbsId/images")
        val url = fileService.uploadFile(file, dir)

        // BBS 엔티티에 이미지 추가 (이미 BBS에 addImage 메서드 만들어 둔 상태)
        bbs.addImage(
            url = url,
            isPrimary = isPrimary,
            caption = caption,
            sortOrder = null
        )

        val savedImage = bbs.images.maxBy { it.id }

        return BbsImageResponse(
            id = savedImage.id,
            url = savedImage.url,
            isPrimary = savedImage.isPrimary,
            caption = savedImage.caption,
            sortOrder = savedImage.sortOrder
        )
    }
}