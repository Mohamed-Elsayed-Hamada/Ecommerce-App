package com.example.e_commerce.data.auth.models
import com.google.gson.annotations.SerializedName

data class SignResponseModel(
    @SerializedName("message")
    //success ,fail for sign up , Incorrect email or password for sign in
    val message: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: User
)

data class User(
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("role")
    val role: String
)


