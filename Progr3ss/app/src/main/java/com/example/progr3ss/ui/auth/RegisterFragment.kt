package com.example.progr3ss.ui.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.progr3ss.R
import com.example.progr3ss.databinding.FragmentRegisterBinding
import com.example.progr3ss.utils.SessionManager

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // A hibajelzés eltűnik, amint a felhasználó javítani kezd
        binding.etUsername.doOnTextChanged { _, _, _, _ -> binding.tilUsername.error = null }
        binding.etEmail.doOnTextChanged { _, _, _, _ -> binding.tilEmail.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null
        }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null
        }

        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                viewModel.register(
                    binding.etUsername.text.toString().trim(),
                    binding.etEmail.text.toString().trim(),
                    binding.etPassword.text.toString()
                )
            }
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            Log.d("RegisterFragment", "registerResult: $result")
            result.onSuccess { authResponse ->
                SessionManager(requireContext().applicationContext)
                    .saveAuthToken(authResponse.tokens.accessToken)

                Toast.makeText(
                    requireContext(),
                    "Welcome ${authResponse.user.profile.username}",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            }.onFailure { error ->
                Toast.makeText(
                    requireContext(),
                    "Registration failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        var valid = true

        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            valid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            valid = false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            valid = false
        }

        if (confirm.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            valid = false
        } else if (password.isNotEmpty() && password != confirm) {
            // Mindkét mező piros keretet kap, és megmondja, mi a gond
            binding.tilPassword.error = "Passwords do not match"
            binding.tilConfirmPassword.error = "Passwords do not match"
            valid = false
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}