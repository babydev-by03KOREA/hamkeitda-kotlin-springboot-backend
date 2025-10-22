package com.hamkeitda.app.server.store

interface RefreshTokenStore {
    fun save(userId: Long, token: String, ttlSec: Long)
    fun exists(userId: Long, token: String): Boolean
    fun replace(userId: Long, oldToken: String, newToken: String, ttlSec: Long)
    fun delete(userId: Long, token: String)
}