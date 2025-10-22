package com.hamkeitda.app.server.common.jwt

import com.hamkeitda.app.server.role.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-validity-ms}") private val accessTokenValidityMs: Long,
    @Value("\${jwt.refresh-token-validity-ms}") private val refreshTokenValidityMs: Long,
) {
    private val key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createAccessToken(userId: Long, role: UserRole): String {
        val now = Date()
        val exp = Date(now.time + accessTokenValidityMs)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", role.value)
            .issuedAt(now)
            .expiration(exp)
            .signWith(key, Jwts.SIG.HS512)
            .compact()
    }

    fun createRefreshToken(userId: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("typ", "refresh")
            .issuedAt(now)
            .expiration(Date(now.time + refreshTokenValidityMs))
            .signWith(key, Jwts.SIG.HS512)
            .compact()
    }

    fun getUserIdFromToken(token: String): Long =
        parseClaims(token).subject.toLong()

    fun getRoleFromToken(token: String): UserRole {
        val claims = parseClaims(token)
        return UserRole(claims["role"].toString())
    }

    /**
     * 토큰 유효성 검증
     */
    fun validateToken(token: String?): Boolean {
        try {
            if (!isCompactJwt(token)) return false
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            return true
        } catch (e: JwtException) {
            // 로그 기록 등 예외 처리
            return false
        } catch (e: IllegalArgumentException) {
            return false
        }
    }

    /**
     * 토큰 유효성 검증 및 클레임 추출
     *
     * @param token 검증할 JWT 토큰
     * @return 유효한 경우 Claims 객체 반환
     */
    private fun parseClaims(token: String): Claims {
        require(isCompactJwt(token)) { "JWT 포맷이 아님(빈 값이거나 마침표 2개 미만)" }
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun isCompactJwt(token: String?): Boolean {
        var token = token ?: return false
        token = token.trim { it <= ' ' }
        if (token.isEmpty()) return false
        // compact JWS 포맷: 마침표 2개
        val dots = token.chars().filter { c: Int -> c == '.'.code }.count()
        return dots == 2L
    }

    fun accessTtlSeconds(): Long = accessTokenValidityMs / 1000
    fun refreshTtlSeconds(): Long = refreshTokenValidityMs / 1000

    fun getRoleFromRefresh(token: String): UserRole {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        // refresh에도 role을 싣고 싶다면 createRefreshToken에 claim 추가해서 사용
        val role = (claims["role"] ?: "ROLE_USER").toString()
        return UserRole(role)
    }
}