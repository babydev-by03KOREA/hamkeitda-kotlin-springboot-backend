package com.hamkeitda.app.server.common.config

import com.hamkeitda.app.server.common.jwt.JwtAuthenticationFilter
import com.hamkeitda.app.server.common.jwt.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwt: JwtTokenProvider
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/**", "/api/public/**").permitAll() // 게스트
                    .requestMatchers("/api/facility/**").hasAuthority("ROLE_FACILITY")
                    .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(jwt), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}