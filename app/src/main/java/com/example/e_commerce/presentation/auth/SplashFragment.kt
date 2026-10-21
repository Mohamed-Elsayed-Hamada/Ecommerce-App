package com.example.e_commerce.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.core.TokenStore
import com.example.e_commerce.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenStore: TokenStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgLogo.alpha = 0f
        binding.imgLogo.animate().alpha(1f).setDuration(800).start()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(SPLASH_DELAY_MS)

            // The view can be torn down during the delay; isAdded alone does not
            // guarantee that a NavController is still reachable.
            if (_binding == null || getView() == null || !isAdded) return@launch

            val navController = runCatching { findNavController() }.getOrNull() ?: return@launch
            if (navController.currentDestination?.id != R.id.splashFragment) return@launch

            val action = if (tokenStore.getToken() != null) {
                SplashFragmentDirections.actionSplashFragmentToHomeyFragment()
            } else {
                SplashFragmentDirections.actionSplashFragmentToSignInFragment()
            }
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val SPLASH_DELAY_MS = 1200L
    }
}
