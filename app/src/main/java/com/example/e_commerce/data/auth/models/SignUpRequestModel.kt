package com.example.e_commerce.data.auth.models
import com.google.gson.annotations.SerializedName
data class SignUpRequestModel(
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("rePassword")
    val rePassword: String
)


