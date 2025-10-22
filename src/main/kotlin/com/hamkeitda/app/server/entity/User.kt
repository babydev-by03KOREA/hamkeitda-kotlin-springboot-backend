package com.hamkeitda.app.server.entity

import com.hamkeitda.app.server.role.UserRole
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false, length = 30)
    val nickname: String,

    @Column(nullable = false, length = 32)
    val role: UserRole = UserRole.USER
) {
    protected constructor() : this(0, "", "", "", UserRole.USER)
}
