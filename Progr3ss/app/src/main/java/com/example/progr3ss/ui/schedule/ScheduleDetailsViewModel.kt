package com.example.progr3ss.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.progr3ss.model.CreateProgressRequest
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.model.UpdateScheduleRequest
import com.example.progr3ss.repository.ScheduleRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.time.LocalDate

class ScheduleDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScheduleRepository(application)

    private val _schedule = MutableLiveData<Result<ScheduleResponse>>()
    val schedule: LiveData<Result<ScheduleResponse>> = _schedule

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Egyszeri események: a feldolgozás után null-ra állítjuk őket,
    // így visszatéréskor nem futnak le újra
    private val _progressResult = MutableLiveData<Result<Unit>?>()
    val progressResult: LiveData<Result<Unit>?> = _progressResult

    private val _notesResult = MutableLiveData<Result<Unit>?>()
    val notesResult: LiveData<Result<Unit>?> = _notesResult

    private val _deleteResult = MutableLiveData<Result<Unit>?>()
    val deleteResult: LiveData<Result<Unit>?> = _deleteResult

    fun loadSchedule(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _schedule.value = Result.success(repository.getScheduleById(id))
            } catch (e: Exception) {
                _schedule.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProgress(scheduleId: Long, loggedTime: Int?, notes: String?, isCompleted: Boolean) {
        val request = CreateProgressRequest(
            scheduleId = scheduleId,
            date = "${LocalDate.now()}T00:00:00.000Z",
            loggedTime = loggedTime,
            notes = notes,
            isCompleted = isCompleted
        )
        runAction(_progressResult) { repository.createProgress(request) }
    }

    fun updateNotes(scheduleId: Long, notes: String) {
        runAction(_notesResult) {
            repository.updateSchedule(scheduleId, UpdateScheduleRequest(notes = notes))
        }
    }

    fun deleteSchedule(scheduleId: Long) {
        runAction(_deleteResult) { repository.deleteSchedule(scheduleId) }
    }

    fun clearProgressResult() { _progressResult.value = null }
    fun clearNotesResult() { _notesResult.value = null }
    fun clearDeleteResult() { _deleteResult.value = null }

    private fun runAction(
        target: MutableLiveData<Result<Unit>?>,
        call: suspend () -> Response<*>
    ) {
        viewModelScope.launch {
            try {
                val response = call()
                if (response.isSuccessful) {
                    target.value = Result.success(Unit)
                } else {
                    val details = response.errorBody()?.string()
                    target.value = Result.failure(
                        Exception("Request failed: ${response.code()} $details")
                    )
                }
            } catch (e: Exception) {
                target.value = Result.failure(e)
            }
        }
    }
}