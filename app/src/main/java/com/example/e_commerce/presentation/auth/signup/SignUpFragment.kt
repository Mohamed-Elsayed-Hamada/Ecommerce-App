package com.example.e_commerce.presentation.auth.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.R
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import com.example.e_commerce.databinding.FragmentSignUpBinding
import com.example.e_commerce.presentation.auth.AuthField
import com.example.e_commerce.presentation.auth.AuthUiState
import com.example.e_commerce.presentation.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        setObservers()
    }

    private fun setUpClickListener() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim().orEmpty()
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            val rePassword = binding.etRePassword.text?.toString().orEmpty()
            val phone = binding.etPhoneNumber.text?.toString()?.trim().orEmpty()

            clearErrors()

            var valid = true
            if (name.isBlank()) {
                binding.tilName.error = getString(R.string.error_fill_all_fields)
                valid = false
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = getString(R.string.error_invalid_email)
                valid = false
            }
            if (phone.count { it.isDigit() } < MIN_PHONE_DIGITS) {
                binding.tilPhoneNumber.error = getString(R.string.error_invalid_phone)
                valid = false
            }
            if (password.length < MIN_PASSWORD_LENGTH) {
                binding.tilPassword.error = resources.getQuantityString(
                    R.plurals.password_min_length,
                    MIN_PASSWORD_LENGTH,
                    MIN_PASSWORD_LENGTH
                )
                valid = false
            }
            if (password != rePassword) {
                binding.tilRePassword.error = getString(R.string.error_passwords_dont_match)
                valid = false
            }

            if (valid) {
                viewModel.signUp(SignUpRequestModel(email, name, password, phone, rePassword))
            } else {
                Snackbar.make(binding.root, R.string.error_check_fields, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.tvSignIn.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility =
                if (state is AuthUiState.Loading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = state !is AuthUiState.Loading

            when (state) {
                is AuthUiState.SignedIn -> {
                    if (findNavController().currentDestination?.id == R.id.signUpFragment) {
                        val action = SignUpFragmentDirections.actionSignUpFragmentToHomeyFragment()
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
            AuthField.RE_PASSWORD -> binding.tilRePassword.error = message
            AuthField.PHONE -> binding.tilPhoneNumber.error = message
            AuthField.NAME -> binding.tilName.error = message
            AuthField.GENERAL -> Unit
        }
    }

    private fun clearErrors() {
        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilRePassword.error = null
        binding.tilPhoneNumber.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
        const val MIN_PHONE_DIGITS = 7
    }
}
