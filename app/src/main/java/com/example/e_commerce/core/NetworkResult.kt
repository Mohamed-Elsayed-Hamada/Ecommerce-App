package com.example.e_commerce.core

/**
 * Typed result for every repository call.
 *
 * Repositories used to return `null` (or a fabricated success object) on failure,
 * which made it impossible for the UI to tell "empty" from "failed".
 */
sealed interface NetworkResult<out T> {

    data class Success<T>(val data: T) : NetworkResult<T>

    data class HttpError(
        val code: Int,
        val message: String
    ) : NetworkResult<Nothing>

    data class NetworkError(val cause: Throwable) : NetworkResult<Nothing>
}
