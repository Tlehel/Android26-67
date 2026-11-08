package com.example.progr3ss.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.progr3ss.model.CreateCustomScheduleRequest
import com.example.progr3ss.model.CreateRecurringScheduleRequest
import com.example.progr3ss.model.HabitResponse
import com.example.progr3ss.repository.HabitRepository
import com.example.progr3ss.repository.ScheduleRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CreateScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val habitRepository = HabitRepository(application)
    private val scheduleRepository = ScheduleRepository(application)

    private val _habits = MutableLiveData<Result<List<HabitResponse>>>()
    val habits: LiveData<Result<List<HabitResponse>>> = _habits

    private val _createResult = MutableLiveData<Result<Unit>>()
    val createResult: LiveData<Result<Unit>> = _createResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadHabits() {
        viewModelScope.launch {
            try {
                _habits.value = Result.success(habitRepository.getHabits())
            } catch (e: Exception) {
                _habits.value = Result.failure(e)
            }
        }
    }

    /**
     * repeatPattern: "none", "daily", "weekdays" vagy "weekends".
     * A "none" egyszeri ütemezés (custom), a többi ismétlődő (recurring).
     */
    fun createSchedule(
        habitId: Long,
        start: LocalDateTime,
        durationMinutes: Int?,
        repeatPattern: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val end = durationMinutes?.let { start.plusMinutes(it.toLong()) }
                val startUtc = toUtcString(start)
                val endUtc = end?.let { toUtcString(it) }

                val response = if (repeatPattern == "none") {
                    scheduleRepository.createCustomSchedule(
                        CreateCustomScheduleRequest(
                            habitId = habitId,
                            date = "${start.toLocalDate()}T00:00:00.000Z",
                            startTime = startUtc,
                            endTime = endUtc,
                            durationMinutes = durationMinutes
                        )
                    )
                } else {
                    scheduleRepository.createRecurringSchedule(
                        CreateRecurringScheduleRequest(
                            habitId = habitId,
                            startTime = startUtc,
                            endTime = endUtc,
                            durationMinutes = durationMinutes,
                            repeatPattern = repeatPattern
                        )
                    )
                }

                if (response.isSuccessful) {
                    _createResult.value = Result.success(Unit)
                } else {
                    val details = response.errorBody()?.string()
                    _createResult.value = Result.failure(
                        Exception("Create schedule failed: ${response.code()} $details")
                    )
                }
            } catch (e: Exception) {
                _createResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // A helyi időt UTC szövegre alakítja, pl. 2026-11-02T16:00:00Z
    private fun toUtcString(local: LocalDateTime): String {
        val instant = local.atZone(ZoneId.systemDefault()).toInstant()
        return DateTimeFormatter.ISO_INSTANT.format(instant)
    }
}