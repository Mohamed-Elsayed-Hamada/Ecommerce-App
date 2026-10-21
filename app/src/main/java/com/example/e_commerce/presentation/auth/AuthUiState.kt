package com.example.e_commerce.presentation.auth

import androidx.annotation.StringRes

enum class AuthField { EMAIL, PASSWORD, RE_PASSWORD, PHONE, NAME, GENERAL }

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object SignedIn : AuthUiState
    data object NetworkError : AuthUiState
    data class FieldError(val field: AuthField, val message: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class ValidationError(@param:StringRes val messageRes: Int, val field: AuthField) : AuthUiState
}
