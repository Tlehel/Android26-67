package com.example.progr3ss.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.model.UpdateScheduleRequest
import com.example.progr3ss.repository.ScheduleRepository
import com.example.progr3ss.utils.toUtcIsoString
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    private val _schedule = MutableLiveData<Result<ScheduleResponse>>()
    val schedule: LiveData<Result<ScheduleResponse>> = _schedule

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    fun loadSchedule(id: Long) {
        viewModelScope.launch {
            try {
                _schedule.value = Result.success(repository.getScheduleById(id))
            } catch (e: Exception) {
                _schedule.value = Result.failure(e)
            }
        }
    }

    fun save(
        id: Long,
        start: LocalDateTime,
        end: LocalDateTime,
        durationMinutes: Int?,
        status: String,
        notes: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = UpdateScheduleRequest(
                    startTime = start.toUtcIsoString(),
                    endTime = end.toUtcIsoString(),
                    durationMinutes = durationMinutes,
                    status = status,
                    date = "${start.toLocalDate()}T00:00:00.000Z",
                    notes = notes
                )
                val response = repository.updateSchedule(id, request)
                if (response.isSuccessful) {
                    _saveResult.value = Result.success(Unit)
                } else {
                    val details = response.errorBody()?.string()
                    _saveResult.value = Result.failure(
                        Exception("Update failed: ${response.code()} $details")
                    )
                }
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}