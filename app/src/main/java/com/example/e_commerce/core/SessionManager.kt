package com.example.e_commerce.core

import androidx.lifecycle.LiveData
import com.example.e_commerce.core.util.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central place for session invalidation. The auth interceptor reports HTTP 401,
 * the token is cleared and the Activity navigates back to sign-in.
 */
@Singleton
class SessionManager @Inject constructor(
    private val tokenStore: TokenStore
) {

    private val _sessionExpired = SingleLiveEvent<Unit>()
    val sessionExpired: LiveData<Unit> = _sessionExpired

    fun onUnauthorized() {
        if (tokenStore.getToken() == null) return
        tokenStore.clearToken()
        _sessionExpired.postEvent(Unit)
    }

    fun logout() {
        tokenStore.clearToken()
        _sessionExpired.postEvent(Unit)
    }
}
