package com.example.progr3ss.repository

import android.content.Context
import com.example.progr3ss.model.ScheduleResponse
import com.example.progr3ss.network.RetrofitClient

class ScheduleRepository(context: Context) {

    private val api by lazy { RetrofitClient.getInstance(context) }

    suspend fun getScheduleByDay(day: String): List<ScheduleResponse> {
        return api.getScheduleByDay(day)
    }
}