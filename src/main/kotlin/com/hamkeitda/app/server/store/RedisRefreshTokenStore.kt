package com.hamkeitda.app.server.store

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisRefreshTokenStore(
    private val redis: StringRedisTemplate
) : RefreshTokenStore {
    private fun key(userId: Long, token: String) = "rt:$userId:$token"

    override fun save(userId: Long, token: String, ttlSec: Long) {
        redis.opsForValue().set(key(userId, token), "1", ttlSec, TimeUnit.SECONDS)
    }

    override fun exists(userId: Long, token: String): Boolean {
        return redis.hasKey(key(userId, token))
    }

    override fun replace(userId: Long, oldToken: String, newToken: String, ttlSec: Long) {
        val oldKey = key(userId, oldToken)
        redis.delete(oldKey)
        save(userId, newToken, ttlSec)
    }

    override fun delete(userId: Long, token: String) {
        redis.delete(key(userId, token))
    }
}