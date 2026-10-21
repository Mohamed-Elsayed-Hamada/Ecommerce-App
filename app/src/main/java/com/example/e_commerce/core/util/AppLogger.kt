package com.example.e_commerce.core.util

import android.util.Log
import com.example.e_commerce.BuildConfig

/**
 * Debug-only logging. Prevents stack traces / payloads from leaking into release logs.
 */
object AppLogger {

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable)
        }
    }

    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}
