package com.example.e_commerce.presentation.auth.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.databinding.FragmentSignInBinding
import com.example.e_commerce.presentation.auth.AuthField
import com.example.e_commerce.presentation.auth.AuthUiState
import com.example.e_commerce.presentation.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setUpClickListener()
    }

    private fun setUpClickListener() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()

            binding.tilEmail.error = null
            binding.tilPassword.error = null

            var valid = true
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = getString(R.string.error_invalid_email)
                valid = false
            }
            if (password.isBlank()) {
                binding.tilPassword.error = getString(R.string.error_fill_all_fields)
                valid = false
            }

            if (valid) {
                viewModel.signIn(SignInRequestModel(email, password))
            }
        }

        binding.tvRegister.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility =
                if (state is AuthUiState.Loading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = state !is AuthUiState.Loading

            when (state) {
                is AuthUiState.SignedIn -> {
                    if (findNavController().currentDestination?.id == R.id.signInFragment) {
                        val action = SignInFragmentDirections.actionSignInFragmentToHomeyFragment()
                        findNavController().navigate(action)
                    }
                }

                is AuthUiState.FieldError -> {
                    showFieldError(state.field, state.message)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                is AuthUiState.ValidationError -> {
                    showFieldError(state.field, getString(state.messageRes))
                    viewModel.resetState()
                }

                is AuthUiState.Error -> {
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                is AuthUiState.NetworkError -> {
                    Snackbar.make(binding.root, R.string.error_network, Snackbar.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                is AuthUiState.Loading, is AuthUiState.Idle -> Unit
            }
        }
    }

    private fun showFieldError(field: AuthField, message: String) {
        when (field) {
            AuthField.EMAIL -> binding.tilEmail.error = message
            AuthField.PASSWORD -> binding.tilPassword.error = message
            AuthField.GENERAL -> Unit
            else -> Unit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
