package com.example.e_commerce.data.auth.models
import com.google.gson.annotations.SerializedName

data class ResetCodeResponseModel(
//      for failure case
//    "statusMsg": "fail",
//    "message": "Reset code is invalid or has expired"

    @SerializedName("message")
    val message: String,
    @SerializedName("statusMsg")
    val statusMsg: String,
    @SerializedName("status")
    //Success in case of success
    val status: String
)


