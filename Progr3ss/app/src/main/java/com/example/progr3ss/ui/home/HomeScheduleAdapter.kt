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
import com.example.progr3ss.utils.CategoryIcons
import java.time.format.DateTimeFormatter

class HomeScheduleAdapter(
    private val onItemClick: (ScheduleResponse) -> Unit
) : ListAdapter<ScheduleResponse, HomeScheduleAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
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

            // Kategória ikon
            binding.ivIcon.setImageResource(CategoryIcons.iconFor(categoryName))

            // Státusz ikon: Completed / Skipped / Planned (minden más)
            binding.ivStatus.setImageResource(statusIcon(item.status))
            binding.ivStatus.contentDescription = item.status ?: "Planned"
        }

        private fun statusIcon(status: String?): Int = when (status) {
            "Completed" -> R.drawable.ic_status_completed
            "Skipped" -> R.drawable.ic_status_skipped
            else -> R.drawable.ic_status_planned
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