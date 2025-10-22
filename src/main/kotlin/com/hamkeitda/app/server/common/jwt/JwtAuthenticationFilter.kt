package com.hamkeitda.app.server.common.jwt

import com.hamkeitda.app.server.role.UserRole
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwt: JwtTokenProvider
) : OncePerRequestFilter() {
    // 요청을 가로채거나, 인증/인가 검사, 로깅, 헤더 조작, CORS 처리, JWT 파싱 등 비즈니스에 맞는 전후 처리
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token: String? = this.resolveToken(request)

        if (token != null && jwt.validateToken(token)) {
            val userId: Long = jwt.getUserIdFromToken(token)
            val userRole: UserRole = jwt.getRoleFromToken(token)

            val auth =
                UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    listOf(SimpleGrantedAuthority(userRole.value))
                )

            // 로그인 감시·감사 로그에 IP/세션 기록 & 세션 제어 및 동시 로그인 제한 & IP 기반 보안 정책 적용
            auth.details = WebAuthenticationDetailsSource().buildDetails(request)

            SecurityContextHolder.getContext().authentication = auth
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        return if (header != null && header.startsWith("Bearer "))
            header.substring(7)
        else
            null
    }
}