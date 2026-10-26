package com.example.progr3ss.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.repository.ScheduleRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ScheduleRepository) : ViewModel() {

    private val _schedules = MutableLiveData<List<ScheduleResponse>>()
    val schedules: LiveData<List<ScheduleResponse>> get() = _schedules

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun getScheduleByDay(day: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getScheduleByDay(day)
                _schedules.value = response
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load schedules"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}