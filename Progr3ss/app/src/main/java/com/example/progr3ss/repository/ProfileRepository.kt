package com.example.progr3ss.repository

import android.content.Context
import com.example.progr3ss.model.HabitResponse
import com.example.progr3ss.model.Profile
import com.example.progr3ss.model.UpdateProfileRequest
import com.example.progr3ss.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class ProfileRepository(context: Context) {

    private val api by lazy { RetrofitClient.getInstance(context) }

    suspend fun getProfile(): Profile = api.getProfile()

    suspend fun getHabitsByUser(userId: Long): List<HabitResponse> =
        api.getHabitsByUser(userId)

    suspend fun updateUsername(username: String): Response<ResponseBody> =
        api.updateProfile(UpdateProfileRequest(username))

    suspend fun uploadProfileImage(bytes: ByteArray, mimeType: String): Response<ResponseBody> {
        val extension = if (mimeType.contains("png")) "png" else "jpg"
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("profileImage", "profile.$extension", body)
        return api.uploadProfileImage(part)
    }

    suspend fun logout(): Response<ResponseBody> = api.logout()
}