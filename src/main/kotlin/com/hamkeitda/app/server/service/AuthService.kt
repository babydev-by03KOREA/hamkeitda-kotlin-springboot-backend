package com.hamkeitda.app.server.service

import com.hamkeitda.app.server.common.exception.ApiException
import com.hamkeitda.app.server.common.jwt.JwtTokenProvider
import com.hamkeitda.app.server.dto.auth.LoginRequest
import com.hamkeitda.app.server.dto.auth.RegisterRequest
import com.hamkeitda.app.server.dto.auth.RegisterResponse
import com.hamkeitda.app.server.dto.auth.TokenPairResponse
import com.hamkeitda.app.server.entity.User
import com.hamkeitda.app.server.entity.facility.Facility
import com.hamkeitda.app.server.repository.UserRepository
import com.hamkeitda.app.server.repository.facility.FacilityRepository
import com.hamkeitda.app.server.role.UserRole
import com.hamkeitda.app.server.store.RefreshTokenStore
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val facilityRepository: FacilityRepository,
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

    @Transactional
    fun register(req: RegisterRequest): RegisterResponse {
        if (userRepository.existsByEmail(req.email)) {
            throw ApiException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.")
        }

        val user = User(
            email = req.email,
            nickname = req.nickname,
            password = passwordEncoder.encode(req.password),
            role = req.role,
            facilityId = null
        )
        val savedUser = userRepository.save(user)

        if (savedUser.role == UserRole.FACILITY) {
            val facility = facilityRepository.save(
                Facility(
                    // 최소 필수 컬럼만 맞춰서 생성 (나머지는 기본값/nullable)
                    name = "${savedUser.nickname}님의 시설",
                    openTime = LocalTime.of(9, 0),
                    closedTime = LocalTime.of(18, 0),
                    phoneNumber = "",
                    address = "",
                    description = "",
                )
            )

            savedUser.facilityId = facility.id
            userRepository.save(savedUser)
        }

        return RegisterResponse(
            id = savedUser.id,
            email = savedUser.email,
            nickname = savedUser.nickname,
            role = savedUser.role.value,
            facilityId = savedUser.facilityId
        )
    }

    fun rotate(refreshToken: String): TokenPairResponse {
        val userId = jwt.getUserIdFromToken(refreshToken)

        if (!refreshStore.exists(userId, refreshToken)) {
            throw ApiException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.")
        }

        // 여기서 유저를 다시 조회해서 role/name/facilityId를 확정
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