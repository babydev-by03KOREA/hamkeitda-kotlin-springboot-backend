package com.hamkeitda.app.server.dto.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserPrincipal(
    val userId: Long,
    val role: String
) : UserDetails {
    override fun getAuthorities() = emptyList<GrantedAuthority>()
    override fun getPassword() = ""
    override fun getUsername() = userId.toString()
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}