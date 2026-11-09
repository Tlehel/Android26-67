package com.example.progr3ss.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.progr3ss.databinding.FragmentEditProfileBinding
import com.example.progr3ss.model.Profile
import com.example.progr3ss.utils.loadAvatar

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var initialized = false

    // Képválasztó a telefon galériájából
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.uploadImage(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etUsername.doOnTextChanged { _, _, _, _ -> binding.tilUsername.error = null }

        binding.btnChangePhoto.setOnClickListener { pickImage.launch("image/*") }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener { saveUsername() }

        viewModel.profile.observe(viewLifecycleOwner) { result ->
            result.onSuccess { showProfile(it) }
                .onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "Could not load profile: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !loading
            binding.btnChangePhoto.isEnabled = !loading
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            result.onSuccess {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                viewModel.clearUpdateResult()
                findNavController().popBackStack()
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                viewModel.clearUpdateResult()
            }
        }

        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            result.onSuccess {
                Toast.makeText(requireContext(), "Photo updated", Toast.LENGTH_SHORT).show()
                viewModel.loadProfile()   // az új kép megjelenik
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Upload failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
            viewModel.clearUploadResult()
        }

        viewModel.loadProfile()
    }

    private fun showProfile(profile: Profile) {
        binding.ivAvatar.loadAvatar(profile.profileImageUrl)
        binding.etEmail.setText(profile.email ?: "")

        // A felhasználónév mezőt csak egyszer töltjük ki, hogy a gépelést ne írjuk felül
        if (!initialized) {
            binding.etUsername.setText(profile.username ?: "")
            initialized = true
        }
    }

    private fun saveUsername() {
        val username = binding.etUsername.text.toString().trim()
        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            return
        }
        viewModel.updateUsername(username)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}