package com.example.e_commerce.presentation.auth.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.e_commerce.MainActivity
import com.example.e_commerce.data.auth.models.SignUpRequestModel
import com.example.e_commerce.databinding.FragmentSignUpBinding
import com.example.e_commerce.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
        setObservers()
    }
    private fun setUpClickListener(){
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val passworld = binding.etPassword.text.toString()
            val rePassworld = binding.etRePassword.text.toString()
            val phone = binding.etPhoneNumber.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && passworld.isNotEmpty() && rePassworld.isNotEmpty()) {
                if(passworld == rePassworld){
                    viewModel.signUp(SignUpRequestModel(email, name, passworld, phone, rePassworld))
                }else{
                    binding.tilRePassword.error = "Passwords do not match"
                }
            }
            else
            {
                binding.tilName.error = "please fill all fields"
                binding.tilEmail.error = "please fill all fields"
                binding.tilPassword.error = "please fill all fields"
                binding.tilRePassword.error = "please fill all fields"
                binding.tilPhoneNumber.error = "please fill all fields"

            }
        }
    }
    private fun setObservers(){
        viewModel.signInResponse.observe(viewLifecycleOwner) { response ->
            if (response.message.lowercase() == "success") {
                val intet = Intent(requireContext(), MainActivity::class.java)
                startActivity(intet)
            }
        }
        viewModel.errorFlag.observe(viewLifecycleOwner){

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