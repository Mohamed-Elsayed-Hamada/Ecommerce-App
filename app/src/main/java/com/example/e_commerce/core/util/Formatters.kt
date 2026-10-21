package com.example.e_commerce.core.util

import android.content.Context
import com.example.e_commerce.R
import java.text.NumberFormat
import java.util.Locale

/**
 * Money and rating formatting shared by every adapter/screen so the same value
 * renders identically everywhere and respects the device locale.
 */
fun formatMoney(context: Context, amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
    }
    return context.getString(R.string.price_format, formatter.format(amount))
}

fun formatRating(rating: Double): String =
    String.format(Locale.getDefault(), "%.1f", rating)
