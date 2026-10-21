package com.example.e_commerce.data.auth

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignResponseModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import com.example.e_commerce.data.auth.models.User
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private val request = SignInRequestModel("user@example.com", "secret123")

    @Test
    fun `success body maps to Success`() = runTest {
        val repository = AuthRepository(
            FakeAuthApiService(
                signInResponse = Response.success(successBody())
            )
        )

        val result = repository.signIn(request)

        assertEquals(NetworkResult.Success(successBody()), result)
    }

    @Test
    fun `http 400 maps to HttpError with server message`() = runTest {
        val repository = AuthRepository(
            FakeAuthApiService(
                signInResponse = Response.error(
                    400,
                    """{"statusMsg":"fail","message":"Incorrect email or password"}"""
                        .toResponseBody("application/json".toMediaType())
                )
            )
        )

        val result = repository.signIn(request)

        assertTrue(result is NetworkResult.HttpError)
        result as NetworkResult.HttpError
        assertEquals(400, result.code)
        assertEquals("Incorrect email or password", result.message)
    }

    @Test
    fun `two hundred with fail message is not treated as success`() = runTest {
        val repository = AuthRepository(
            FakeAuthApiService(
                signInResponse = Response.success(
                    successBody(message = "fail")
                )
            )
        )

        val result = repository.signIn(request)

        assertTrue(result is NetworkResult.HttpError)
        assertEquals("fail", (result as NetworkResult.HttpError).message)
    }

    private fun successBody(message: String = "success") = SignResponseModel(
        message = message,
        token = "token-123",
        user = User("user@example.com", "User", "user")
    )
}

private class FakeAuthApiService(
    private val signInResponse: Response<SignResponseModel>,
    private val signUpResponse: Response<SignResponseModel> = signInResponse
) : AuthApiService {

    override suspend fun signUp(signUpRequest: SignUpRequestModel): Response<SignResponseModel> =
        signUpResponse

    override suspend fun signIn(signInRequest: SignInRequestModel): Response<SignResponseModel> =
        signInResponse
}
