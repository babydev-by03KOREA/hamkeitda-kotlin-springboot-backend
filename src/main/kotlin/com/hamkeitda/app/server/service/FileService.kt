package com.hamkeitda.app.server.service

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class FileService(
    private val s3: AmazonS3
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val BUCKET_NAME = "hamkeitda"
    private val DIRECTORY_SEPARATOR = "/"

    /**
     * S3에 파일 업로드
     */
    fun uploadFile(
        file: MultipartFile,
        dirName: String,
        isPublic: Boolean = true
    ): String {
        val extension = file.originalFilename?.substringAfterLast('.', "") ?: "dat"
        val uniqueName = "${UUID.randomUUID()}.$extension"
        val fileKey = "$dirName/$uniqueName"

        val metadata = ObjectMetadata().apply {
            contentType = file.contentType ?: "application/octet-stream"
            contentLength = file.size
        }

        try {
            val request = PutObjectRequest(BUCKET_NAME, fileKey, file.inputStream, metadata)
            if (isPublic) request.cannedAcl = CannedAccessControlList.PublicRead

            s3.putObject(request)
            val url = s3.getUrl(BUCKET_NAME, fileKey).toString()

            log.info("[UPLOAD_SUCCESS] fileKey=$fileKey, url=$url, size=${file.size}")
            return url

        } catch (e: AmazonServiceException) {
            log.error("[UPLOAD_FAIL] key=$fileKey, reason=${e.errorMessage}", e)
            throw RuntimeException("파일 업로드 실패: ${e.errorMessage}", e)
        }
    }

    /**
     * S3에서 단일 파일 삭제
     */
    fun deleteFile(url: String, dirName: String) {
        val fileName = url.substringAfterLast("/")
        val fileKey = "$dirName/$fileName"

        try {
            val request = DeleteObjectRequest(BUCKET_NAME, fileKey)
            s3.deleteObject(request)
            log.info("[DELETE_SUCCESS] key=$fileKey")

        } catch (e: AmazonServiceException) {
            log.error("[DELETE_FAIL] key=$fileKey, reason=${e.errorMessage}", e)
            throw RuntimeException("파일 삭제 실패: ${e.errorMessage}", e)
        }
    }

    /**
     * 여러 파일 한 번에 삭제 (성능 개선용)
     */
    fun deleteFiles(keys: List<String>) {
        if (keys.isEmpty()) return

        try {
            val deleteReq = DeleteObjectsRequest(BUCKET_NAME).withKeys(*keys.toTypedArray())
            s3.deleteObjects(deleteReq)
            log.info("[DELETE_BATCH_SUCCESS] count=${keys.size}")

        } catch (e: AmazonServiceException) {
            log.error("[DELETE_BATCH_FAIL] keys=${keys.joinToString()}, reason=${e.errorMessage}", e)
            throw RuntimeException("여러 파일 삭제 실패: ${e.errorMessage}", e)
        }
    }

    /**
     * 비동기 업로드 (대용량 파일 업로드 시)
     */
    @Async
    fun asyncUpload(file: MultipartFile, dirName: String) {
        uploadFile(file, dirName)
    }

    /**
     * 파일 경로 Prefix 자동화
     */
    fun buildFilePath(category: String, id: Long, subDir: String? = null): String {
        return listOfNotNull(category, id.toString(), subDir)
            .joinToString(DIRECTORY_SEPARATOR)
    }
}
