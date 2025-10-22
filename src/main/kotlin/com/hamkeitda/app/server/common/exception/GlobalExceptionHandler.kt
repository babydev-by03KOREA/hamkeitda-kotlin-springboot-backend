package com.hamkeitda.app.server.common.exception

import com.hamkeitda.app.server.common.ErrorResponse
import com.hamkeitda.app.server.common.Result
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    /** 비즈니스 커스텀 예외 */
    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException, request: HttpServletRequest): ResponseEntity<Result.Err> {
        val body = Result.Err(
            status = e.status.value(),
            code = e.code,
            message = e.message
        )
        return ResponseEntity(body, e.status)
    }

    /** 스프링 바인딩 예외(org.springframework.validation.BindException) — override 불가, 직접 핸들링 */
    @ExceptionHandler(BindException::class)
    fun handleBindExceptionDirect(ex: BindException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val first = ex.bindingResult.fieldErrors.firstOrNull()
        val message = first?.let { "${it.field}: ${it.defaultMessage}" }
            ?: "요청 값 바인딩에 실패했습니다."
        val body = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "BINDING_ERROR",
            message = message,
            path = request.requestURI
        )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    /** 그 외 모든 예외(최후 방어) */
    @ExceptionHandler(Exception::class)
    fun handleUncaught(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            code = "INTERNAL_SERVER_ERROR",
            message = e.message ?: "서버 내부 오류가 발생했습니다.",
            path = request.requestURI
        )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /** @Valid 바인딩 실패 - 존재하는 메서드이므로 override 가능 (visibility: protected) */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val firstError = ex.bindingResult.fieldErrors.firstOrNull()
        val message = firstError?.let { "${it.field}: ${it.defaultMessage}" }
            ?: "요청 값이 유효하지 않습니다."
        val body = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "VALIDATION_ERROR",
            message = message
        )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    /** 쿼리 파라미터 누락 - 존재하는 메서드이므로 override 가능 (visibility: protected) */
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val body = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            code = "MISSING_PARAMETER",
            message = "필수 파라미터가 누락되었습니다: ${ex.parameterName}"
        )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }
}
