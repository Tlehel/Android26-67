package com.example.progr3ss.ui.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.progr3ss.R
import com.example.progr3ss.model.HabitResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.progr3ss.databinding.FragmentCreateScheduleBinding

class CreateScheduleFragment : Fragment() {

    private var _binding: FragmentCreateScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateScheduleViewModel by viewModels()

    private var habits: List<HabitResponse> = emptyList()
    private var selectedHabit: HabitResponse? = null
    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedTime: LocalTime =
        LocalTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0)

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDateTimeButtons()

        binding.etDuration.doOnTextChanged { _, _, _, _ -> binding.tilDuration.error = null }

        binding.actvHabit.setOnItemClickListener { _, _, position, _ ->
            selectedHabit = habits[position]
            binding.tilHabit.error = null
        }

        binding.btnNewHabit.setOnClickListener {
            findNavController().navigate(R.id.action_createScheduleFragment_to_addHabitFragment)
        }

        binding.btnDate.setOnClickListener { showDatePicker() }
        binding.btnTime.setOnClickListener { showTimePicker() }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnCreate.setOnClickListener { createSchedule() }

        // A szokások listája minden megjelenéskor frissül (az Add Habitból visszatérve is)
        viewModel.loadHabits()
        viewModel.habits.observe(viewLifecycleOwner) { result ->
            result.onSuccess { list ->
                habits = list
                binding.actvHabit.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        list.map { it.name }
                    )
                )
                selectedHabit?.let { binding.actvHabit.setText(it.name, false) }
                if (list.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "No habits yet, create one first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.onFailure { error ->
                Toast.makeText(
                    requireContext(),
                    "Could not load habits: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnCreate.isEnabled = !loading
        }

        viewModel.createResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Schedule created", Toast.LENGTH_SHORT).show()
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

    private fun updateDateTimeButtons() {
        binding.btnDate.text = selectedDate.toString()
        binding.btnTime.text = selectedTime.format(timeFormatter)
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)
                updateDateTimeButtons()
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                selectedTime = LocalTime.of(hour, minute)
                updateDateTimeButtons()
            },
            selectedTime.hour,
            selectedTime.minute,
            true
        ).show()
    }

    private fun createSchedule() {
        var valid = true

        val habit = selectedHabit
        if (habit == null) {
            binding.tilHabit.error = "Please choose a habit"
            valid = false
        }

        val durationText = binding.etDuration.text.toString().trim()
        var duration: Int? = null
        if (durationText.isNotEmpty()) {
            duration = durationText.toIntOrNull()
            if (duration == null || duration <= 0) {
                binding.tilDuration.error = "Enter a positive number of minutes"
                valid = false
            }
        }

        if (!valid || habit == null) return

        val repeatPattern = when (binding.rgRepeat.checkedRadioButtonId) {
            R.id.rbDaily -> "daily"
            R.id.rbWeekdays -> "weekdays"
            R.id.rbWeekends -> "weekends"
            else -> "none"
        }

        viewModel.createSchedule(
            habitId = habit.id,
            start = LocalDateTime.of(selectedDate, selectedTime),
            durationMinutes = duration,
            repeatPattern = repeatPattern
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}