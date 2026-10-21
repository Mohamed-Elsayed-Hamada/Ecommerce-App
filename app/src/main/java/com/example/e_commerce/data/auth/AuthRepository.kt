package com.example.e_commerce.data.auth

import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.toNetworkResult
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignResponseModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: AuthApiService) {

    suspend fun signIn(request: SignInRequestModel): NetworkResult<SignResponseModel> =
        apiService.signIn(request).toAuthResult()

    suspend fun signUp(request: SignUpRequestModel): NetworkResult<SignResponseModel> =
        apiService.signUp(request).toAuthResult()

    /**
     * Single success predicate for both flows: HTTP 2xx AND the body's message is "success".
     * Any other 2xx body is reported as an HTTP error carrying the server message.
     */
    private suspend fun Response<SignResponseModel>.toAuthResult(): NetworkResult<SignResponseModel> {
        return when (val result = toNetworkResult()) {
            is NetworkResult.Success -> {
                val message = result.data.message.trim()
                if (message.equals("success", ignoreCase = true)) {
                    result
                } else {
                    NetworkResult.HttpError(code(), message.ifBlank { "Authentication failed" })
                }
            }

            is NetworkResult.HttpError -> result
            is NetworkResult.NetworkError -> result
        }
    }
}
