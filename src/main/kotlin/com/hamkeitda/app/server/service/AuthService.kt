package com.hamkeitda.app.server.service

import com.hamkeitda.app.server.common.exception.ApiException
import com.hamkeitda.app.server.common.jwt.JwtTokenProvider
import com.hamkeitda.app.server.dto.LoginRequest
import com.hamkeitda.app.server.dto.RegisterRequest
import com.hamkeitda.app.server.dto.RegisterResponse
import com.hamkeitda.app.server.dto.TokenPairResponse
import com.hamkeitda.app.server.entity.User
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

        // 서버 저장소에 refresh 보관(회전/로그아웃/블랙리스트 용도)
        refreshStore.save(user.id, refresh, jwt.refreshTtlSeconds())

        return TokenPairResponse(
            accessToken = access,
            refreshToken = refresh,
            expiresIn = jwt.accessTtlSeconds(),
            refreshExpiresIn = jwt.refreshTtlSeconds(),
            role = user.role.value
        )
    }

    fun register(req: RegisterRequest): RegisterResponse {
        if (userRepository.existsByEmail(req.email)) {
            throw ApiException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.")
        }

        val encodedPw = passwordEncoder.encode(req.password)
        val user = User(
            email = req.email,
            password = encodedPw,
            nickname = req.nickname,
            role = req.role
        )
        val saved = userRepository.save(user)

        return RegisterResponse(
            id = saved.id,
            email = saved.email,
            nickname = saved.nickname,
            role = saved.role.value
        )
    }

    fun rotate(refreshToken: String): TokenPairResponse {
        val userId = jwt.getUserIdFromToken(refreshToken)

        // 저장소에 있는지/만료됐는지 확인 (토큰 로테이션 대비)
        if (!refreshStore.exists(userId, refreshToken)) {
            throw ApiException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.")
        }

        val newAccess = jwt.createAccessToken(userId, jwt.getRoleFromToken(refreshToken))
        val newRefresh = jwt.createRefreshToken(userId)

        // 회전: 기존 것을 폐기하고 새 토큰 저장
        refreshStore.replace(userId, oldToken = refreshToken, newToken = newRefresh, ttlSec = jwt.refreshTtlSeconds())

        return TokenPairResponse(
            accessToken = newAccess,
            refreshToken = newRefresh,
            expiresIn = jwt.accessTtlSeconds(),
            refreshExpiresIn = jwt.refreshTtlSeconds(),
            role = jwt.getRoleFromRefresh(refreshToken).value
        )
    }

    fun logout(refreshToken: String) {
        val userId = jwt.getUserIdFromToken(refreshToken)
        refreshStore.delete(userId, refreshToken)
    }
}