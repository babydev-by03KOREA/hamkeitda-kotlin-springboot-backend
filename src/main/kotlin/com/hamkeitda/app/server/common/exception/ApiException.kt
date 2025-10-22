package com.hamkeitda.app.server.common.exception

import org.springframework.http.HttpStatus

/**
 * 서비스 전반에서 쓰는 커스텀 예외.
 * 필요하면 code(비즈니스 에러코드)도 추가해두면 좋다.
 */
open class ApiException(
    val status: HttpStatus,
    override val message: String,
    val code: String? = null
) : RuntimeException(message)