package com.example.e_commerce.core.util

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

/**
 * Guarded navigation: ignores taps that arrive while the fragment is no longer
 * the current destination (double taps / stale listeners).
 */
fun Fragment.navigateSafely(
    directions: NavDirections,
    @IdRes fromDestinationId: Int? = null
) {
    val navController = findNavController()
    val currentId = navController.currentDestination?.id ?: return
    if (fromDestinationId != null && currentId != fromDestinationId) return
    navController.navigate(directions)
}
