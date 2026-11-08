package com.example.progr3ss.network

import com.example.progr3ss.model.AuthRequest
import com.example.progr3ss.model.AuthResponse
import com.example.progr3ss.model.CreateCustomScheduleRequest
import com.example.progr3ss.model.CreateHabitRequest
import com.example.progr3ss.model.CreateProgressRequest
import com.example.progr3ss.model.CreateRecurringScheduleRequest
import com.example.progr3ss.model.HabitCategory
import com.example.progr3ss.model.HabitResponse
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.model.UpdateScheduleRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
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
    ): Response<AuthResponse>

    // A dátum YYYY-MM-DD formátumú
    @GET("schedule/day")
    suspend fun getScheduleByDay(@Query("date") day: String): List<ScheduleResponse>

    @GET("habit/categories")
    suspend fun getHabitCategories(): List<HabitCategory>

    @POST("habit")
    suspend fun createHabit(@Body request: CreateHabitRequest): Response<HabitResponse>

    @GET("habit")
    suspend fun getHabits(): List<HabitResponse>

    @POST("schedule/custom")
    suspend fun createCustomSchedule(@Body request: CreateCustomScheduleRequest): Response<ResponseBody>

    @POST("schedule/recurring")
    suspend fun createRecurringSchedule(@Body request: CreateRecurringScheduleRequest): Response<ResponseBody>

    @GET("schedule/{id}")
    suspend fun getScheduleById(@Path("id") id: Long): ScheduleResponse

    @POST("progress")
    suspend fun createProgress(@Body request: CreateProgressRequest): Response<ResponseBody>

    @PATCH("schedule/{id}")
    suspend fun updateSchedule(
        @Path("id") id: Long,
        @Body request: UpdateScheduleRequest
    ): Response<ResponseBody>

    @DELETE("schedule/{id}")
    suspend fun deleteSchedule(@Path("id") id: Long): Response<Unit>
}