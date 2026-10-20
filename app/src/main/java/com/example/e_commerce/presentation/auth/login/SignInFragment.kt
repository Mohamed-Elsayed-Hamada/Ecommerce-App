package com.example.e_commerce.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.data.auth.models.SignInRequestModel
import com.example.e_commerce.databinding.FragmentSignInBinding
import com.example.e_commerce.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            val email = binding.etEmail.text.toString()
            val passworld = binding.etPassword.text.toString()
            if (email.isNotEmpty() && passworld.isNotEmpty()) {
                viewModel.signIn(SignInRequestModel(email, passworld))
            } else {
                Toast.makeText(requireContext(), "please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.tvRegister.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    private fun setObservers() {
        viewModel.signInResponse.observe(viewLifecycleOwner) { response ->
            if (response.message.lowercase(Locale.getDefault()) == "success") {
                binding.tilEmail.error = null
                binding.tilPassword.error = null
                var action = SignInFragmentDirections.actionSignInFragmentToHomeyFragment()
                findNavController().navigate(action)
            } else {
                binding.tilEmail.error = "Incorrect email or password"
                binding.tilPassword.error = "Incorrect email or password"
            }
            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
        }
        viewModel.errorFlag.observe(viewLifecycleOwner) { error ->
            if (error) {
                Toast.makeText(requireContext(), viewModel.errorMessage.value, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

    }

}