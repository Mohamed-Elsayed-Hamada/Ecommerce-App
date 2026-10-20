package com.example.e_commerce.data.auth.models
import com.google.gson.annotations.SerializedName

data class ForgetPasswordResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("statusMsg")
    //fail , success
    val statusMsg: String
)


