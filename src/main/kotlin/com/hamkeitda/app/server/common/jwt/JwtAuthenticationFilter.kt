package com.hamkeitda.app.server.common.jwt

import com.hamkeitda.app.server.dto.auth.CustomUserPrincipal
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

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        if (token != null && jwt.validateToken(token)) {
            val userId: Long = jwt.getUserIdFromToken(token)
            val userRole: UserRole = jwt.getRoleFromToken(token)

            val principal = CustomUserPrincipal(userId, userRole.value)
            val authorities = listOf(SimpleGrantedAuthority(userRole.value))

            val auth = UsernamePasswordAuthenticationToken(principal, null, authorities)
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