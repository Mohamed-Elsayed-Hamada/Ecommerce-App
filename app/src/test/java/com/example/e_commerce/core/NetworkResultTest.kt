package com.example.e_commerce.core

import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class NetworkResultTest {

    @Test
    fun `successful response is wrapped in Success`() {
        val result = Response.success("payload").toNetworkResult()

        assertEquals(NetworkResult.Success("payload"), result)
    }

    @Test
    fun `http error keeps status code and server message`() {
        val response = Response.error<String>(
            400,
            """{"statusMsg":"fail","message":"Incorrect email or password"}"""
                .toResponseBody("application/json".toMediaType())
        )

        val result = response.toNetworkResult()

        assertTrue(result is NetworkResult.HttpError)
        result as NetworkResult.HttpError
        assertEquals(400, result.code)
        assertEquals("Incorrect email or password", result.message)
    }

    @Test
    fun `non json error body falls back to status code message`() {
        val response = Response.error<String>(
            500,
            "boom".toResponseBody("text/plain".toMediaType())
        )

        val result = response.toNetworkResult()

        assertTrue(result is NetworkResult.HttpError)
        assertEquals("Request failed (500)", (result as NetworkResult.HttpError).message)
    }

    @Test
    fun `error payload parser prefers message over statusMsg`() {
        val json = JsonParser.parseString(
            """{"statusMsg":"fail","message":"Phone already exists"}"""
        ).asJsonObject

        assertEquals("Phone already exists", json.get("message").asString)
    }
}
