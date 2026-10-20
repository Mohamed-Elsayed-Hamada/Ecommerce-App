package com.example.e_commerce.data.auth

import com.example.e_commerce.data.auth.models.ForgetPasswordResponse
import com.example.e_commerce.data.products.models.ProductsResponse
import com.example.e_commerce.data.auth.models.ResetCodeResponseModel
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignResponseModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("v1/auth/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequestModel): Response<SignResponseModel>
    @POST("v1/auth/signin")
    suspend fun signIn(@Body signInRequest: SignInRequestModel): Response<SignResponseModel>
    @POST("v1/auth/forgotPasswords")
    suspend fun forgotPassword(@Body email: String): Response<ForgetPasswordResponse>
    @POST("v1/auth/verifyResetCode")
    suspend fun verifyResetCode(@Body resetCode: String): Response<ResetCodeResponseModel>
    @POST("v1/auth/resetPassword")
    suspend fun resetPassword(@Body email : String, @Body newPassword: String): Response<String> //token

}