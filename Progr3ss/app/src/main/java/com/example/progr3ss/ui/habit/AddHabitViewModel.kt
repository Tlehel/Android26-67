package com.example.progr3ss.ui.habit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.progr3ss.model.HabitCategory
import com.example.progr3ss.model.HabitResponse
import com.example.progr3ss.repository.HabitRepository
import kotlinx.coroutines.launch

class AddHabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HabitRepository(application)

    private val _categories = MutableLiveData<Result<List<HabitCategory>>>()
    val categories: LiveData<Result<List<HabitCategory>>> = _categories

    private val _createResult = MutableLiveData<Result<HabitResponse>>()
    val createResult: LiveData<Result<HabitResponse>> = _createResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = Result.success(repository.getCategories())
            } catch (e: Exception) {
                _categories.value = Result.failure(e)
            }
        }
    }

    fun createHabit(name: String, description: String?, categoryId: Long, goal: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.createHabit(name, description, categoryId, goal)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _createResult.value = Result.success(body)
                } else {
                    val details = response.errorBody()?.string()
                    _createResult.value = Result.failure(
                        Exception("Create habit failed: ${response.code()} $details")
                    )
                }
            } catch (e: Exception) {
                _createResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}