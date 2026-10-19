package com.example.progr3ss.repository

import android.content.Context
import com.example.progr3ss.model.AuthRequest
import com.example.progr3ss.network.RetrofitClient
import okhttp3.MultipartBody

class AuthRepository(context: Context) {

    private val api = RetrofitClient.getInstance(context)

    suspend fun login(email: String, password: String) =
        api.login(AuthRequest(email = email, password = password))

    suspend fun register(username: String, email: String, password: String) =
        api.signUp(
            username = MultipartBody.Part.createFormData("username", username),
            email = MultipartBody.Part.createFormData("email", email),
            password = MultipartBody.Part.createFormData("password", password)
        )
}