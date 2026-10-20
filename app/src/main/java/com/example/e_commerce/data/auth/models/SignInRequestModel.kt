package com.example.e_commerce.data.auth.models
import com.google.gson.annotations.SerializedName
data class SignInRequestModel(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)


