package com.example.progr3ss.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.progr3ss.R
import com.example.progr3ss.databinding.FragmentProfileBinding
import com.example.progr3ss.model.HabitResponse
import com.example.progr3ss.model.Profile
import com.example.progr3ss.utils.CategoryIcons
import com.example.progr3ss.utils.loadAvatar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.btnAddHabit.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addHabitFragment)
        }
        binding.btnLogout.setOnClickListener { showLogoutDialog() }

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

        viewModel.habits.observe(viewLifecycleOwner) { result ->
            result.onSuccess { showHabits(it) }
                .onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "Could not load habits: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        viewModel.logoutDone.observe(viewLifecycleOwner) { done ->
            if (done == true) {
                viewModel.clearLogoutDone()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
        }

        // Minden megjelenéskor frissül (szerkesztés és új szokás után is)
        viewModel.loadProfile()
    }

    private fun showProfile(profile: Profile) {
        binding.tvUsername.text = profile.username ?: "(no username)"
        binding.tvEmail.text = profile.email ?: ""
        binding.ivAvatar.loadAvatar(profile.profileImageUrl)

        val description = profile.description
        if (description.isNullOrBlank()) {
            binding.tvDescription.visibility = View.GONE
        } else {
            binding.tvDescription.text = description
            binding.tvDescription.visibility = View.VISIBLE
        }
    }

    private fun showHabits(habits: List<HabitResponse>) {
        binding.llHabits.removeAllViews()
        binding.tvNoHabits.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE

        habits.forEach { habit ->
            val categoryName = habit.category?.name
            var text = habit.name
            if (categoryName != null) text += "  •  $categoryName"
            if (!habit.goal.isNullOrBlank()) text += "\n${habit.goal}"

            val row = TextView(requireContext()).apply {
                this.text = text
                setPadding(0, 16, 0, 16)
                compoundDrawablePadding = 24
                setCompoundDrawablesWithIntrinsicBounds(
                    CategoryIcons.iconFor(categoryName), 0, 0, 0
                )
                compoundDrawableTintList = textColors
            }
            binding.llHabits.addView(row)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log out") { _, _ -> viewModel.logout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}