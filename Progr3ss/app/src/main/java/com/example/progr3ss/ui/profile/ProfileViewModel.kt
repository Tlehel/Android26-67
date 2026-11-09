package com.example.progr3ss.ui.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.progr3ss.model.HabitResponse
import com.example.progr3ss.model.Profile
import com.example.progr3ss.repository.ProfileRepository
import com.example.progr3ss.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)
    private val session = SessionManager(application)

    private val _profile = MutableLiveData<Result<Profile>>()
    val profile: LiveData<Result<Profile>> = _profile

    private val _habits = MutableLiveData<Result<List<HabitResponse>>>()
    val habits: LiveData<Result<List<HabitResponse>>> = _habits

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Egyszeri események: feldolgozás után null-ra állítjuk őket
    private val _updateResult = MutableLiveData<Result<Unit>?>()
    val updateResult: LiveData<Result<Unit>?> = _updateResult

    private val _uploadResult = MutableLiveData<Result<Unit>?>()
    val uploadResult: LiveData<Result<Unit>?> = _uploadResult

    private val _logoutDone = MutableLiveData<Boolean?>()
    val logoutDone: LiveData<Boolean?> = _logoutDone

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.getProfile()
                _profile.value = Result.success(profile)

                // A szokások listája a mentett felhasználó azonosítóval (ha nincs, a profiléval)
                val userId = session.fetchUserId() ?: profile.id.toLong()
                _habits.value = try {
                    Result.success(repository.getHabitsByUser(userId))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            } catch (e: Exception) {
                _profile.value = Result.failure(e)
            }
        }
    }

    fun updateUsername(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _updateResult.value = toResult(repository.updateUsername(username))
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resolver = getApplication<Application>().contentResolver
                val mimeType = resolver.getType(uri) ?: "image/jpeg"
                val bytes = withContext(Dispatchers.IO) {
                    resolver.openInputStream(uri)?.use { it.readBytes() }
                }
                if (bytes == null) {
                    _uploadResult.value = Result.failure(Exception("Could not read the image"))
                } else {
                    _uploadResult.value = toResult(repository.uploadProfileImage(bytes, mimeType))
                }
            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // A helyi kijelentkezés akkor is megtörténik, ha a szerver hívás nem sikerül
    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                // a helyi törlés mindenképp lefut
            }
            session.clearSession()
            _logoutDone.value = true
        }
    }

    fun clearUpdateResult() { _updateResult.value = null }
    fun clearUploadResult() { _uploadResult.value = null }
    fun clearLogoutDone() { _logoutDone.value = null }

    private fun toResult(response: Response<*>): Result<Unit> {
        return if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            val details = response.errorBody()?.string()
            Result.failure(Exception("Request failed: ${response.code()} $details"))
        }
    }
}