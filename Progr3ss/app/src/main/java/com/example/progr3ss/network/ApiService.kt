package com.example.progr3ss.network

import com.example.progr3ss.model.AuthRequest
import com.example.progr3ss.model.AuthResponse
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.model.SignUpResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("auth/local/signin")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @Multipart
    @POST("auth/local/signup")
    suspend fun signUp(
        @Part username: MultipartBody.Part,
        @Part email: MultipartBody.Part,
        @Part password: MultipartBody.Part
    ): Response<SignUpResponse>

    // A dátum YYYY-MM-DD formátumú
    @GET("schedule/day")
    suspend fun getScheduleByDay(@Query("date") day: String): List<ScheduleResponse>
}