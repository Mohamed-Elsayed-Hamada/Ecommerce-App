package com.example.e_commerce.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.core.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            if (isAdded) {
                val token = TokenManager(requireContext())

                if (token.getToken() != null) {
                    // Token exists -> Navigate to Home
                    val action = SplashFragmentDirections.actionSplashFragmentToHomeyFragment()
                    findNavController().navigate(action)
                } else {
                    // Token is null -> Navigate to Sign In
                    val action = SplashFragmentDirections.actionSplashFragmentToSignInFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }

}