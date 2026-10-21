package com.example.e_commerce.core

/** Abstraction over token persistence so ViewModels can be tested without Android. */
interface TokenStore {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
