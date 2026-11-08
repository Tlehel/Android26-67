package com.example.progr3ss.repository

import android.content.Context
import com.example.progr3ss.model.CreateCustomScheduleRequest
import com.example.progr3ss.model.CreateProgressRequest
import com.example.progr3ss.model.CreateRecurringScheduleRequest
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.model.UpdateScheduleRequest
import com.example.progr3ss.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Response

class ScheduleRepository(context: Context) {

    private val api by lazy { RetrofitClient.getInstance(context) }

    suspend fun getScheduleByDay(day: String): List<ScheduleResponse> {
        return api.getScheduleByDay(day)
    }

    suspend fun createCustomSchedule(request: CreateCustomScheduleRequest): Response<ResponseBody> {
        return api.createCustomSchedule(request)
    }

    suspend fun createRecurringSchedule(request: CreateRecurringScheduleRequest): Response<ResponseBody> {
        return api.createRecurringSchedule(request)
    }

    suspend fun getScheduleById(id: Long): ScheduleResponse {
        return api.getScheduleById(id)
    }

    suspend fun createProgress(request: CreateProgressRequest): Response<ResponseBody> {
        return api.createProgress(request)
    }

    suspend fun updateSchedule(id: Long, request: UpdateScheduleRequest): Response<ResponseBody> {
        return api.updateSchedule(id, request)
    }

    suspend fun deleteSchedule(id: Long): Response<Unit> {
        return api.deleteSchedule(id)
    }
}