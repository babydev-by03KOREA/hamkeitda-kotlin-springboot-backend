package com.hamkeitda.app.server.common

sealed interface Result<out T> {
    data class Ok<T>(val data: T) : Result<T>
    data class Err(
        val status: Int,
        val code: String? = null,
        val message: String
    ) : Result<Nothing>
}