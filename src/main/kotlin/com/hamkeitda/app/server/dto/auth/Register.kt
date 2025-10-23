package com.hamkeitda.app.server.dto.auth

import com.hamkeitda.app.server.role.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수입니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    /*@field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,64}$",
        message = "비밀번호는 영문과 숫자를 모두 포함해야 합니다."
    )*/
    val password: String,

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Size(min = 2, max = 30, message = "닉네임은 2~30자 사이여야 합니다.")
    val nickname: String,

    val role: UserRole = UserRole.USER
)

data class RegisterResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: String
)