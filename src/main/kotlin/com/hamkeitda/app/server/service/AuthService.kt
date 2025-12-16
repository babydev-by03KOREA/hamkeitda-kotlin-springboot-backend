package com.hamkeitda.app.server.service

import com.hamkeitda.app.server.common.exception.ApiException
import com.hamkeitda.app.server.common.jwt.JwtTokenProvider
import com.hamkeitda.app.server.dto.auth.LoginRequest
import com.hamkeitda.app.server.dto.auth.TokenPairResponse
import com.hamkeitda.app.server.repository.UserRepository
import com.hamkeitda.app.server.store.RefreshTokenStore
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwt: JwtTokenProvider,
    private val refreshStore: RefreshTokenStore
) {

    fun login(req: LoginRequest): TokenPairResponse {
        val user = userRepository.findByEmail(req.email)
            ?: throw ApiException(HttpStatus.UNAUTHORIZED, "이메일이 존재하지 않습니다.")

        if (!passwordEncoder.matches(req.password, user.password)) {
            throw ApiException(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.")
        }

        val access = jwt.createAccessToken(user.id, user.role)
        val refresh = jwt.createRefreshToken(user.id)

        refreshStore.save(user.id, refresh, jwt.refreshTtlSeconds())

        return TokenPairResponse(
            accessToken = access,
            refreshToken = refresh,
            expiresIn = jwt.accessTtlSeconds(),
            refreshExpiresIn = jwt.refreshTtlSeconds(),
            role = user.role.value,
            userId = user.id,
            name = user.nickname,
            facilityId = user.facilityId,
        )
    }

    fun rotate(refreshToken: String): TokenPairResponse {
        val userId = jwt.getUserIdFromToken(refreshToken)

        if (!refreshStore.exists(userId, refreshToken)) {
            throw ApiException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.")
        }

        // ✅ 여기서 유저를 다시 조회해서 role/name/facilityId를 확정
        val user = userRepository.findById(userId)
            .orElseThrow { ApiException(HttpStatus.UNAUTHORIZED, "유저가 존재하지 않습니다.") }

        val newAccess = jwt.createAccessToken(user.id, user.role)
        val newRefresh = jwt.createRefreshToken(user.id)

        refreshStore.replace(
            userId = user.id,
            oldToken = refreshToken,
            newToken = newRefresh,
            ttlSec = jwt.refreshTtlSeconds()
        )

        return TokenPairResponse(
            accessToken = newAccess,
            refreshToken = newRefresh,
            expiresIn = jwt.accessTtlSeconds(),
            refreshExpiresIn = jwt.refreshTtlSeconds(),
            role = user.role.value,
            userId = user.id,
            name = user.nickname,
            facilityId = user.facilityId,
        )
    }

    fun logout(refreshToken: String) {
        val userId = jwt.getUserIdFromToken(refreshToken)
        refreshStore.delete(userId, refreshToken)
    }
}