package com.example.e_commerce.presentation.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.core.TokenManager
import com.example.e_commerce.data.auth.AuthRepository
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignResponseModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository, private val tokenManager: TokenManager) : ViewModel() {
    private val _signInResponse = MutableLiveData<SignResponseModel>()
    val signInResponse: LiveData<SignResponseModel>
        get() = _signInResponse
    val errorFlag = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    fun signUp(request : SignUpRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            errorFlag.postValue(false)
            try {
                var result = authRepository.sign_Up(request)
                if(result.message == "success"){
                    tokenManager.saveToken(result.token)
                }
                _signInResponse.postValue(result)
            }catch (ex : Exception){
                var errorMessageVar =ex.message ?: "Unknown Error"
                errorMessage.postValue(errorMessageVar)
                errorFlag.postValue(true)
                Log.e("error",ex.message.toString())
            }
            finally {
                isLoading.postValue(false)
            }
        }
    }
     fun signIn(request : SignInRequestModel) {

        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            errorFlag.postValue(false)
            try {
                var result  = authRepository.sing_In(request)
                if(result.message != "fail"){
                    tokenManager.saveToken(result.token)
                }
                _signInResponse.postValue(result)
            }
            catch (ex : Exception ){
                var errorMessageVar =ex.message ?: "Unknown Error"
                errorMessage.postValue(errorMessageVar)
                errorFlag.postValue(true)
                Log.e("error",ex.message.toString())
            }
            finally {
                isLoading.postValue(false)
            }

        }

    }

}