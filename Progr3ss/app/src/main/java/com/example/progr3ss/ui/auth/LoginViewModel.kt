package com.example.progr3ss.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.progr3ss.model.AuthResponse
import com.example.progr3ss.model.SignUpResponse
import com.example.progr3ss.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    // Login
    private val _authResult = MutableLiveData<Result<AuthResponse>>()
    val authResult: LiveData<Result<AuthResponse>> = _authResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                handleResponse(response)
            } catch (e: Exception) {
                _authResult.postValue(Result.failure(e))
            }
        }
    }

    private fun handleResponse(response: Response<AuthResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _authResult.postValue(Result.success(response.body()!!))
        } else {
            _authResult.postValue(
                Result.failure(Exception("Auth failed: ${response.code()}"))
            )
        }
    }

    // Register
    private val _registerResult = MutableLiveData<Result<SignUpResponse>>()
    val registerResult: LiveData<Result<SignUpResponse>> = _registerResult

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(username, email, password)
                if (response.isSuccessful) {
                    _registerResult.postValue(
                        Result.success(response.body() ?: SignUpResponse(null))
                    )
                } else {
                    val details = response.errorBody()?.string()
                    _registerResult.postValue(
                        Result.failure(
                            Exception("Sign up failed: ${response.code()} $details")
                        )
                    )
                }
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            }
        }
    }
}