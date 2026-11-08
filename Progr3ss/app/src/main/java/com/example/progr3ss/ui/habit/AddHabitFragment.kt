package com.example.progr3ss.ui.habit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.progr3ss.databinding.FragmentAddHabitBinding
import com.example.progr3ss.model.HabitCategory
import com.example.progr3ss.utils.CategoryIcons
import com.google.android.material.chip.Chip

class AddHabitFragment : Fragment() {

    private var _binding: FragmentAddHabitBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddHabitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // A hibajelzés eltűnik, amint a felhasználó javítani kezd
        binding.etName.doOnTextChanged { _, _, _, _ -> binding.tilName.error = null }
        binding.etGoal.doOnTextChanged { _, _, _, _ -> binding.tilGoal.error = null }

        binding.btnSave.setOnClickListener { saveHabit() }

        // Kategóriák betöltése
        viewModel.loadCategories()
        viewModel.categories.observe(viewLifecycleOwner) { result ->
            result.onSuccess { showCategories(it) }
                .onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "Could not load categories: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !loading
        }

        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { habit ->
                Toast.makeText(
                    requireContext(),
                    "Habit \"${habit.name}\" created",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }.onFailure { error ->
                Toast.makeText(
                    requireContext(),
                    "Failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showCategories(categories: List<HabitCategory>) {
        binding.chipGroup.removeAllViews()
        categories.forEach { category ->
            val chip = Chip(requireContext()).apply {
                id = View.generateViewId()
                text = category.name
                tag = category.id
                isCheckable = true
                chipIcon = ContextCompat.getDrawable(
                    requireContext(),
                    CategoryIcons.iconFor(category.name)
                )
                isChipIconVisible = true
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun saveHabit() {
        val name = binding.etName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val goal = binding.etGoal.text.toString().trim()

        var valid = true
        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            valid = false
        }
        if (goal.isEmpty()) {
            binding.tilGoal.error = "Goal is required"
            valid = false
        }

        val checkedId = binding.chipGroup.checkedChipId
        if (checkedId == View.NO_ID) {
            Toast.makeText(requireContext(), "Please choose a category", Toast.LENGTH_SHORT).show()
            valid = false
        }

        if (!valid) return

        val chip = binding.chipGroup.findViewById<Chip>(checkedId)
        val categoryId = chip.tag as Long

        viewModel.createHabit(
            name = name,
            description = description.ifEmpty { null },
            categoryId = categoryId,
            goal = goal
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}