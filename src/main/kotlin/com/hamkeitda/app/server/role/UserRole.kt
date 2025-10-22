package com.hamkeitda.app.server.role

/**
 * @description
 * 딱 하나의 프로퍼티만 감싸는 경량 래퍼
 * “성능 손실 없는 enum-like 타입”을 만드는 현대적인 설계 패턴
 * */
@JvmInline
value class UserRole(val value: String) {
    companion object {  // == static
        val USER = UserRole("ROLE_USER")
        val FACILITY = UserRole("ROLE_FACILITY")
    }
}