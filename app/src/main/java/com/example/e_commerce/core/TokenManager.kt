package com.example.e_commerce.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext
    context: Context
) {

    private val pref =
        context.getSharedPreferences(
            "auth_pref",
            Context.MODE_PRIVATE
        )

    fun saveToken(token: String) {

        pref.edit()
            .putString("TOKEN", token)
            .apply()

    }

    fun getToken(): String? {

        return pref.getString("TOKEN", null)

    }

    fun clearToken() {

        pref.edit().clear().apply()

    }

}