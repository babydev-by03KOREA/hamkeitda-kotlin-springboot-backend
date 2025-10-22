package com.hamkeitda.app.server.controller

import com.hamkeitda.app.server.common.Result
import com.hamkeitda.app.server.dto.LoginRequest
import com.hamkeitda.app.server.dto.RegisterRequest
import com.hamkeitda.app.server.dto.TokenPairResponse
import com.hamkeitda.app.server.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    private val log = LoggerFactory.getLogger(AuthController::class.java)

    /**
     * 회원가입
     */
    @PostMapping("/register")
    fun register(
        @RequestBody @Valid req: RegisterRequest,
        request: HttpServletRequest
    ): Result.Ok<Map<String, Any>> {
        val result = authService.register(req)
        log.info("[REGISTER] email={}, nickname={}, role={}, ip={}",
            result.email, result.nickname, result.role, request.remoteAddr)

        return Result.Ok(
            mapOf(
                "user" to result,
                "message" to "회원가입이 완료되었습니다."
            )
        )
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    fun login(
        @RequestBody @Valid req: LoginRequest,
        request: HttpServletRequest
    ): Result.Ok<TokenPairResponse> {
        val start = System.currentTimeMillis()
        val ip = request.remoteAddr
        val ua = request.getHeader("User-Agent") ?: "unknown"

        val tokenPair = try {
            authService.login(req)
        } catch (e: Exception) {
            val took = System.currentTimeMillis() - start
            log.warn(
                "[LOGIN_FAIL] email={}, ip={}, ua={}, took={}ms, reason={}",
                req.email, ip, ua, took, e.message
            )
            throw e
        }

        val took = System.currentTimeMillis() - start
        log.info(
            "[LOGIN_SUCCESS] email={}, role={}, ip={}, ua={}, took={}ms",
            req.email, tokenPair.role, ip, ua, took
        )

        return Result.Ok(tokenPair)
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") token: String,
        request: HttpServletRequest
    ): Result.Ok<Map<String, String>> {
        val refreshToken = token.removePrefix("Bearer ").trim()
        authService.logout(refreshToken)
        log.info("[LOGOUT] token={}, ip={}", refreshToken.take(10) + "...", request.remoteAddr)

        return Result.Ok(mapOf("message" to "로그아웃이 완료되었습니다."))
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/refresh")
    fun refresh(
        @RequestHeader("Authorization") token: String,
        request: HttpServletRequest
    ): Result.Ok<TokenPairResponse> {
        val refreshToken = token.removePrefix("Bearer ").trim()
        val result = authService.rotate(refreshToken)
        log.info("[TOKEN_REFRESH] ip={}, newAccessToken={}", request.remoteAddr, result.accessToken.take(20) + "...")
        return Result.Ok(result)
    }
}
