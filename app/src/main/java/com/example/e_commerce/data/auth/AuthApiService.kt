package com.example.e_commerce.data.auth

import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignResponseModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("v1/auth/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequestModel): Response<SignResponseModel>

    @POST("v1/auth/signin")
    suspend fun signIn(@Body signInRequest: SignInRequestModel): Response<SignResponseModel>
}
