package com.example.progr3ss.ui.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.progr3ss.R
import com.example.progr3ss.databinding.FragmentEditScheduleBinding
import com.example.progr3ss.model.ScheduleResponse
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EditScheduleFragment : Fragment() {

    private var _binding: FragmentEditScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditScheduleViewModel by viewModels()

    private var scheduleId: Long = -1L
    private var initialized = false

    private var startDate: LocalDate = LocalDate.now()
    private var startTime: LocalTime = LocalTime.of(8, 0)
    private var endTime: LocalTime = LocalTime.of(8, 30)

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleId = requireArguments().getLong("scheduleId", -1L)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateButtons()

        binding.etDuration.doOnTextChanged { _, _, _, _ -> binding.tilDuration.error = null }

        binding.btnStartDate.setOnClickListener { showDatePicker() }
        binding.btnStartTime.setOnClickListener {
            showTimePicker(startTime) { startTime = it; onTimesChanged() }
        }
        binding.btnEndTime.setOnClickListener {
            showTimePicker(endTime) { endTime = it; onTimesChanged() }
        }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener { save() }

        // Az űrlap az első betöltéskor kitöltődik a szerveren lévő adatokkal
        viewModel.schedule.observe(viewLifecycleOwner) { result ->
            result.onSuccess { schedule ->
                if (!initialized) {
                    fillForm(schedule)
                    initialized = true
                }
            }.onFailure { error ->
                Toast.makeText(
                    requireContext(),
                    "Could not load schedule: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !loading
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Schedule updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loadSchedule(scheduleId)
    }

    private fun fillForm(schedule: ScheduleResponse) {
        val duration = schedule.durationMinutes
        val start = schedule.startTime ?: LocalDateTime.now()
        val end = schedule.endTime ?: start.plusMinutes((duration ?: 30).toLong())

        startDate = start.toLocalDate()
        startTime = start.toLocalTime()
        endTime = end.toLocalTime()
        updateButtons()

        binding.etDuration.setText(duration?.toString() ?: minutesBetween().toString())

        when (schedule.status) {
            "Completed" -> binding.rbCompleted.isChecked = true
            "Skipped" -> binding.rbSkipped.isChecked = true
            else -> binding.rbPlanned.isChecked = true
        }

        binding.etNotes.setText(schedule.notes ?: "")
    }

    private fun updateButtons() {
        binding.btnStartDate.text = startDate.toString()
        binding.btnStartTime.text = startTime.format(timeFormatter)
        binding.btnEndTime.text = endTime.format(timeFormatter)
    }

    private fun minutesBetween(): Long = Duration.between(startTime, endTime).toMinutes()

    // Ha az időpontok változnak, az időtartam is követi (utána kézzel módosítható)
    private fun onTimesChanged() {
        updateButtons()
        val minutes = minutesBetween()
        if (minutes > 0) binding.etDuration.setText(minutes.toString())
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                startDate = LocalDate.of(year, month + 1, day)
                updateButtons()
            },
            startDate.year,
            startDate.monthValue - 1,
            startDate.dayOfMonth
        ).show()
    }

    private fun showTimePicker(initial: LocalTime, onPicked: (LocalTime) -> Unit) {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute -> onPicked(LocalTime.of(hour, minute)) },
            initial.hour,
            initial.minute,
            true
        ).show()
    }

    private fun save() {
        val durationText = binding.etDuration.text.toString().trim()
        var duration: Int? = null
        if (durationText.isNotEmpty()) {
            duration = durationText.toIntOrNull()
            if (duration == null || duration <= 0) {
                binding.tilDuration.error = "Enter a positive number of minutes"
                return
            }
        }

        val start = LocalDateTime.of(startDate, startTime)
        val end = LocalDateTime.of(startDate, endTime)
        if (!end.isAfter(start)) {
            Toast.makeText(
                requireContext(),
                "End time must be after start time",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val status = when (binding.rgStatus.checkedRadioButtonId) {
            R.id.rbCompleted -> "Completed"
            R.id.rbSkipped -> "Skipped"
            else -> "Planned"
        }

        viewModel.save(
            id = scheduleId,
            start = start,
            end = end,
            durationMinutes = duration,
            status = status,
            notes = binding.etNotes.text.toString().trim()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}