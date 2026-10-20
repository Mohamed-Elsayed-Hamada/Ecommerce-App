package com.example.e_commerce.data.auth

import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignResponseModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import com.example.e_commerce.data.auth.models.User
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService: AuthApiService ) {
    suspend fun sing_In(SignInRequestModel: SignInRequestModel): SignResponseModel {
        val response = apiService.signIn(SignInRequestModel)
        if(response.isSuccessful){
            return response.body()!!
        }
        else{
            var model = SignResponseModel("fail", "", User("", "", ""))
            return model
        }
    }
    suspend fun sign_Up(SignUpRequestModel: SignUpRequestModel): SignResponseModel {
        val response = apiService.signUp(SignUpRequestModel)
        if(response.isSuccessful){
            return response.body()!!
        }
        var model = SignResponseModel("fail", "", User("", "", ""))
        return model

    }
}