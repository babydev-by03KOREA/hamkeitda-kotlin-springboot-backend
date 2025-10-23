package com.hamkeitda.app.server.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일을 입력하세요.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력하세요.")
    @field:Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    /*@field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+=-]).{8,64}$",
        message = "비밀번호는 영문/숫자/특수문자를 포함해야 합니다."
    )*/
    val password: String
)


data class TokenPairResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,         // access 만료(초)
    val refreshExpiresIn: Long,  // refresh 만료(초)
    val role: String             // UserRole.value
)