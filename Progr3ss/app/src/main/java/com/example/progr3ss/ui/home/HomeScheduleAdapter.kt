package com.example.progr3ss.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.progr3ss.R
import com.example.progr3ss.databinding.ItemHomeScheduleBinding
import com.example.progr3ss.model.ScheduleResponse
import java.time.format.DateTimeFormatter

class HomeScheduleAdapter :
    ListAdapter<ScheduleResponse, HomeScheduleAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHomeScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        fun bind(item: ScheduleResponse) {
            val habit = item.habit

            // Név
            binding.tvTitle.text = habit?.name ?: "Unknown Habit"

            // Leírás
            val description = habit?.description
            if (description.isNullOrBlank()) {
                binding.tvDescription.visibility = View.GONE
            } else {
                binding.tvDescription.text = description
                binding.tvDescription.visibility = View.VISIBLE
            }

            // Idő + kategória
            val start = item.startTime?.format(timeFormatter)
            val end = item.endTime?.format(timeFormatter)
            val timeText = when {
                start != null && end != null -> "$start - $end"
                start != null -> start
                else -> "No time set"
            }
            val categoryName = habit?.category?.name
            binding.tvTime.text =
                if (categoryName != null) "$timeText  •  $categoryName" else timeText

            // Jegyzet
            val notes = item.notes
            if (notes.isNullOrBlank()) {
                binding.tvNotes.visibility = View.GONE
            } else {
                binding.tvNotes.text = notes
                binding.tvNotes.visibility = View.VISIBLE
            }

            // Ikon a kategória alapján
            binding.ivIcon.setImageResource(iconFor(categoryName))
        }

        private fun iconFor(categoryName: String?): Int = when (categoryName) {
            "Exercise" -> R.drawable.ic_exercise
            "Reading" -> R.drawable.ic_reading
            "Study" -> R.drawable.ic_study
            "Hydration" -> R.drawable.ic_hydration
            "Writing" -> R.drawable.ic_writing
            "Running" -> R.drawable.ic_running
            "Other" -> R.drawable.ic_other
            else -> R.drawable.ic_add
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ScheduleResponse>() {
        override fun areItemsTheSame(oldItem: ScheduleResponse, newItem: ScheduleResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ScheduleResponse, newItem: ScheduleResponse): Boolean {
            return oldItem == newItem
        }
    }
}