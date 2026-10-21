package com.example.e_commerce.presentation.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.e_commerce.MainDispatcherRule
import com.example.e_commerce.core.TokenStore
import com.example.e_commerce.data.auth.AuthApiService
import com.example.e_commerce.data.auth.AuthRepository
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignResponseModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import com.example.e_commerce.data.auth.models.User
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `successful sign in stores token and emits SignedIn`() = runTest {
        val tokenStore = FakeTokenStore()
        val viewModel = AuthViewModel(
            AuthRepository(
                StubAuthApi(
                    Response.success(
                        SignResponseModel(
                            message = "success",
                            token = "token-abc",
                            user = User("user@example.com", "User", "user")
                        )
                    )
                )
            ),
            tokenStore
        )

        viewModel.signIn(SignInRequestModel("user@example.com", "secret123"))

        assertEquals(AuthUiState.SignedIn, viewModel.state.value)
        assertEquals("token-abc", tokenStore.storedToken)
    }

    @Test
    fun `incorrect credentials emit a global error, not a field error`() = runTest {
        val viewModel = AuthViewModel(
            AuthRepository(
                StubAuthApi(
                    Response.error(
                        401,
                        """{"statusMsg":"fail","message":"Incorrect email or password"}"""
                            .toResponseBody("application/json".toMediaType())
                    )
                )
            ),
            FakeTokenStore()
        )

        viewModel.signIn(SignInRequestModel("user@example.com", "wrong"))

        val state = viewModel.state.value
        assertEquals(AuthUiState.Error("Incorrect email or password"), state)
    }

    @Test
    fun `duplicate email maps to the email field`() = runTest {
        val viewModel = AuthViewModel(
            AuthRepository(
                StubAuthApi(
                    Response.error(
                        400,
                        """{"statusMsg":"fail","message":"Email already exists"}"""
                            .toResponseBody("application/json".toMediaType())
                    )
                )
            ),
            FakeTokenStore()
        )

        viewModel.signUp(
            SignUpRequestModel(
                email = "user@example.com",
                name = "User",
                password = "secret123",
                phone = "01000000000",
                rePassword = "secret123"
            )
        )

        assertEquals(
            AuthUiState.FieldError(AuthField.EMAIL, "Email already exists"),
            viewModel.state.value
        )
    }
}

private class FakeTokenStore : TokenStore {
    var storedToken: String? = null

    override fun saveToken(token: String) {
        storedToken = token
    }

    override fun getToken(): String? = storedToken

    override fun clearToken() {
        storedToken = null
    }
}

private class StubAuthApi(
    private val response: Response<SignResponseModel>
) : AuthApiService {

    override suspend fun signUp(signUpRequest: SignUpRequestModel): Response<SignResponseModel> =
        response

    override suspend fun signIn(signInRequest: SignInRequestModel): Response<SignResponseModel> =
        response
}
