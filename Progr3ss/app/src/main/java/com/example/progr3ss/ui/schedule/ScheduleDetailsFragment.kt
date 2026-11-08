package com.example.progr3ss.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.progr3ss.R
import com.example.progr3ss.databinding.DialogAddProgressBinding
import com.example.progr3ss.databinding.FragmentScheduleDetailsBinding
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.utils.CategoryIcons
import java.time.format.DateTimeFormatter

class ScheduleDetailsFragment : Fragment() {

    private var _binding: FragmentScheduleDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleDetailsViewModel by viewModels()

    private var scheduleId: Long = -1L
    private var currentNotes: String? = null
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleId = requireArguments().getLong("scheduleId", -1L)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()

        binding.btnAddProgress.setOnClickListener { showAddProgressDialog() }

        // Jegyzet szerkesztő mód
        binding.btnEditNotes.setOnClickListener { enterNotesEditMode() }
        binding.btnCancelNotes.setOnClickListener { exitNotesEditMode() }
        binding.btnSaveNotes.setOnClickListener {
            viewModel.updateNotes(scheduleId, binding.etNotes.text.toString().trim())
        }

        viewModel.schedule.observe(viewLifecycleOwner) { result ->
            result.onSuccess { showSchedule(it) }
                .onFailure { error ->
                    Toast.makeText(
                        requireContext(),
                        "Could not load schedule: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressLoading.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.progressResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            result.onSuccess {
                Toast.makeText(requireContext(), "Progress saved", Toast.LENGTH_SHORT).show()
                viewModel.loadSchedule(scheduleId)
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
            viewModel.clearProgressResult()
        }

        viewModel.notesResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            result.onSuccess {
                Toast.makeText(requireContext(), "Notes saved", Toast.LENGTH_SHORT).show()
                exitNotesEditMode()
                viewModel.loadSchedule(scheduleId)
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
            viewModel.clearNotesResult()
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            result.onSuccess {
                Toast.makeText(requireContext(), "Schedule deleted", Toast.LENGTH_SHORT).show()
                viewModel.clearDeleteResult()
                findNavController().popBackStack()
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_LONG).show()
                viewModel.clearDeleteResult()
            }
        }

        viewModel.loadSchedule(scheduleId)
    }

    // Beállítások menü (három pont): szerkesztés és törlés
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_schedule_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit_schedule -> {
                        findNavController().navigate(
                            R.id.action_scheduleDetailsFragment_to_editScheduleFragment,
                            bundleOf("scheduleId" to scheduleId)
                        )
                        true
                    }
                    R.id.action_delete_schedule -> {
                        showDeleteDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete schedule")
            .setMessage("Are you sure?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteSchedule(scheduleId) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun enterNotesEditMode() {
        binding.etNotes.setText(currentNotes ?: "")
        binding.tvNotes.visibility = View.GONE
        binding.btnEditNotes.visibility = View.GONE
        binding.llNotesEdit.visibility = View.VISIBLE
    }

    private fun exitNotesEditMode() {
        binding.llNotesEdit.visibility = View.GONE
        binding.tvNotes.visibility = View.VISIBLE
        binding.btnEditNotes.visibility = View.VISIBLE
    }

    private fun showSchedule(schedule: ScheduleResponse) {
        val habit = schedule.habit
        val categoryName = habit?.category?.name

        binding.tvHabitName.text = habit?.name ?: "Unknown habit"
        binding.tvCategory.text = categoryName ?: ""
        binding.ivCategory.setImageResource(CategoryIcons.iconFor(categoryName))

        val description = habit?.description
        if (description.isNullOrBlank()) {
            binding.tvDescription.visibility = View.GONE
        } else {
            binding.tvDescription.text = description
            binding.tvDescription.visibility = View.VISIBLE
        }

        val goal = habit?.goal
        if (goal.isNullOrBlank()) {
            binding.tvGoal.visibility = View.GONE
        } else {
            binding.tvGoal.text = "Goal: $goal"
            binding.tvGoal.visibility = View.VISIBLE
        }

        val start = schedule.startTime
        val end = schedule.endTime
        binding.tvDateTime.text = when {
            start != null && end != null ->
                "${start.toLocalDate()}  •  ${start.format(timeFormatter)} - ${end.format(timeFormatter)}"
            start != null ->
                "${start.toLocalDate()}  •  ${start.format(timeFormatter)}"
            else -> "No time set"
        }
        binding.tvStatus.text = "Status: ${schedule.status ?: "Planned"}"

        val percent = completionPercent(schedule)
        binding.progressCompletion.progress = percent
        binding.tvProgressText.text = "$percent%"

        currentNotes = schedule.notes
        binding.tvNotes.text =
            if (schedule.notes.isNullOrBlank()) "No notes" else schedule.notes

        showActivity(schedule)
    }

    // Ha Completed vagy van teljesítettnek jelölt bejegyzés: 100%.
    // Különben a rögzített percek összege az időtartamhoz képest.
    private fun completionPercent(schedule: ScheduleResponse): Int {
        val progress = schedule.progress.orEmpty()
        if (schedule.status == "Completed" || progress.any { it.isCompleted == true }) return 100

        val duration = schedule.durationMinutes ?: return 0
        if (duration <= 0) return 0

        val logged = progress.sumOf { it.loggedTime ?: 0.0 }
        return ((logged / duration) * 100).toInt().coerceIn(0, 100)
    }

    // Legutóbbi bejegyzések (legfeljebb 10), legújabb elöl
    private fun showActivity(schedule: ScheduleResponse) {
        binding.llActivity.removeAllViews()

        val entries = schedule.progress.orEmpty()
            .sortedByDescending { it.date ?: "" }
            .take(10)

        binding.tvNoActivity.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE

        entries.forEach { entry ->
            val parts = mutableListOf(entry.date?.take(10) ?: "-")
            entry.loggedTime?.let { parts.add("${it.toInt()} min") }
            if (entry.isCompleted == true) parts.add("completed")

            var text = parts.joinToString("  •  ")
            if (!entry.notes.isNullOrBlank()) text += "\n${entry.notes}"

            val textView = TextView(requireContext()).apply {
                this.text = text
                setPadding(0, 12, 0, 12)
            }
            binding.llActivity.addView(textView)
        }
    }

    private fun showAddProgressDialog() {
        val dialogBinding = DialogAddProgressBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("Add progress")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val loggedTime = dialogBinding.etLoggedTime.text.toString().trim().toIntOrNull()
                val notes = dialogBinding.etProgressNotes.text.toString().trim().ifEmpty { null }
                viewModel.addProgress(
                    scheduleId = scheduleId,
                    loggedTime = loggedTime,
                    notes = notes,
                    isCompleted = dialogBinding.cbCompleted.isChecked
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}