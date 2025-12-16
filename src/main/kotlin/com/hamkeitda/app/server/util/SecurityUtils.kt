package com.hamkeitda.app.server.util

import com.hamkeitda.app.server.entity.User
import com.hamkeitda.app.server.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class SecurityUtils(
    private val userRepository: UserRepository
) {
    fun currentUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        if (!authentication.isAuthenticated) return null

        val principal = authentication.principal

        val userId: Long? = when (principal) {
            is Long -> principal
            is Int -> principal.toLong()
            is String -> principal.toLongOrNull()
            is UserDetails -> principal.username.toLongOrNull()
            else -> null
        }

        if (userId == null) return null

        return userRepository.findById(userId).orElse(null)
    }
}