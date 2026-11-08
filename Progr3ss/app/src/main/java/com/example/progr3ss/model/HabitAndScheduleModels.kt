package com.example.progr3ss.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class HabitCategory(
    val id: Long,
    val name: String,
    val iconUrl: String? = null
)

data class HabitResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val category: HabitCategory? = null,
    val goal: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class ScheduleResponse(
    val id: Long,
    @SerializedName("start_time")
    val startTime: LocalDateTime? = null,
    @SerializedName("end_time")
    val endTime: LocalDateTime? = null,
    val status: String? = null,
    val date: String? = null,
    @SerializedName("is_custom")
    val isCustom: Boolean = false,
    @SerializedName("created_at")
    val createdAt: LocalDateTime? = null,
    @SerializedName("updated_at")
    val updatedAt: LocalDateTime? = null,
    val type: String? = null,
    @SerializedName("duration_minutes")
    val durationMinutes: Int? = null,
    val notes: String? = null,
    val participants: List<ParticipantDto>? = emptyList(),
    val habit: HabitResponse? = null,
    val progress: List<ProgressResponseDto>? = emptyList(),
    @SerializedName("is_participant_only")
    val isParticipantOnly: Boolean = false
)

data class ProgressResponseDto(
    val id: Long? = null,
    val date: String? = null,
    @SerializedName("logged_time")
    val loggedTime: Double? = null,
    val notes: String? = null,
    @SerializedName("is_completed")
    val isCompleted: Boolean? = null
)

data class CreateProgressRequest(
    val scheduleId: Long,
    val date: String,
    @SerializedName("logged_time")
    val loggedTime: Int? = null,
    val notes: String? = null,
    @SerializedName("is_completed")
    val isCompleted: Boolean? = null
)

data class ParticipantDto(
    val id: Long,
    val name: String? = null,
    val email: String? = null,
    val profileImage: String? = null
)

data class CreateHabitRequest(
    val name: String,
    val description: String?,
    val categoryId: Long,
    val goal: String
)

data class CreateCustomScheduleRequest(
    val habitId: Long,
    val date: String,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String? = null,
    @SerializedName("duration_minutes")
    val durationMinutes: Int? = null,
    @SerializedName("is_custom")
    val isCustom: Boolean = true
)

data class CreateRecurringScheduleRequest(
    val habitId: Long,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String? = null,
    @SerializedName("duration_minutes")
    val durationMinutes: Int? = null,
    val repeatPattern: String,
    @SerializedName("is_custom")
    val isCustom: Boolean = true
)

data class UpdateScheduleRequest(
    @SerializedName("start_time")
    val startTime: String? = null,
    @SerializedName("end_time")
    val endTime: String? = null,
    @SerializedName("duration_minutes")
    val durationMinutes: Int? = null,
    val status: String? = null,
    val date: String? = null,
    val notes: String? = null
)