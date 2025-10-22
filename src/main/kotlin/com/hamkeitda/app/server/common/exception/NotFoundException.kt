package com.hamkeitda.app.server.common.exception

import org.springframework.http.HttpStatus

class NotFoundException(
    message: String = "요청한 리소스를 찾을 수 없습니다.",
    code: String? = "NOT_FOUND"
) : ApiException(HttpStatus.NOT_FOUND, message, code)