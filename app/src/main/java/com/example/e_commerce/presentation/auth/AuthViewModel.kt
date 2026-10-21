package com.example.e_commerce.presentation.auth

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.R
import com.example.e_commerce.core.NetworkResult
import com.example.e_commerce.core.TokenStore
import com.example.e_commerce.data.auth.AuthRepository
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val state: LiveData<AuthUiState> = _state

    fun signIn(request: SignInRequestModel) {
        if (_state.value == AuthUiState.Loading) return

        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            _state.value = when (val result = authRepository.signIn(request)) {
                is NetworkResult.Success -> {
                    tokenStore.saveToken(result.data.token)
                    AuthUiState.SignedIn
                }

                is NetworkResult.HttpError -> mapServerError(result.message)
                is NetworkResult.NetworkError -> AuthUiState.NetworkError
            }
        }
    }

    fun signUp(request: SignUpRequestModel) {
        if (_state.value == AuthUiState.Loading) return

        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            _state.value = when (val result = authRepository.signUp(request)) {
                is NetworkResult.Success -> {
                    tokenStore.saveToken(result.data.token)
                    AuthUiState.SignedIn
                }

                is NetworkResult.HttpError -> mapServerError(result.message)
                is NetworkResult.NetworkError -> AuthUiState.NetworkError
            }
        }
    }

    fun reportValidationError(field: AuthField, @StringRes messageRes: Int) {
        _state.value = AuthUiState.ValidationError(messageRes, field)
    }

    /** Reset after the fragment has consumed an error so rotation does not replay it. */
    fun resetState() {
        if (_state.value != AuthUiState.Loading) {
            _state.value = AuthUiState.Idle
        }
    }

    /**
     * Maps the server message onto the field it refers to when recognizable,
     * otherwise shows it as a global error.
     */
    private fun mapServerError(message: String): AuthUiState {
        val normalized = message.lowercase()
        val field = when {
            normalized.contains("incorrect email or password") -> AuthField.GENERAL
            normalized.contains("email") -> AuthField.EMAIL
            normalized.contains("phone") -> AuthField.PHONE
            normalized.contains("repassword") || normalized.contains("confirm") -> AuthField.RE_PASSWORD
            normalized.contains("password") -> AuthField.PASSWORD
            normalized.contains("name") -> AuthField.NAME
            else -> AuthField.GENERAL
        }

        return if (field == AuthField.GENERAL) {
            AuthUiState.Error(message)
        } else {
            AuthUiState.FieldError(field, message)
        }
    }
}
