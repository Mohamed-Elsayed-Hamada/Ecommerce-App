package com.example.e_commerce.core

import com.google.gson.JsonParser
import retrofit2.Response

/**
 * Single place that turns a Retrofit [Response] into a [NetworkResult].
 * Keeps the HTTP status code and the server-provided message instead of dropping them.
 */
fun <T : Any> Response<T>.toNetworkResult(): NetworkResult<T> {
    if (isSuccessful) {
        val body = body()
        return if (body == null) {
            NetworkResult.HttpError(code(), "Empty response from server")
        } else {
            NetworkResult.Success(body)
        }
    }

    val rawBody = runCatching { errorBody()?.string() }.getOrNull()
    return NetworkResult.HttpError(code(), parseErrorMessage(rawBody, code()))
}

/**
 * RouteMISR error payloads look like `{"statusMsg":"fail","message":"..."}`.
 * Falls back to a generic message when the body is not JSON.
 */
private fun parseErrorMessage(rawBody: String?, code: Int): String {
    if (rawBody.isNullOrBlank()) return "Request failed ($code)"

    val parsed = runCatching {
        val json = JsonParser.parseString(rawBody).asJsonObject
        json.get("message")?.asString
            ?: json.get("statusMsg")?.asString
    }.getOrNull()

    return parsed?.takeIf { it.isNotBlank() } ?: "Request failed ($code)"
}
