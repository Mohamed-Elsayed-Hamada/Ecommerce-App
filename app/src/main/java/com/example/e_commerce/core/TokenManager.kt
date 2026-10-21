package com.example.e_commerce.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext
    context: Context
) : TokenStore {

    private val pref =
        context.getSharedPreferences(
            "auth_pref",
            Context.MODE_PRIVATE
        )

    override fun saveToken(token: String) {
        pref.edit()
            .putString("TOKEN", token)
            .apply()
    }

    override fun getToken(): String? = pref.getString("TOKEN", null)

    override fun clearToken() {
        pref.edit().clear().apply()
    }
}
