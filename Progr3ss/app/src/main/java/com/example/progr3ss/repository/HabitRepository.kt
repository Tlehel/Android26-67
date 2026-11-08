package com.example.progr3ss.repository

import android.content.Context
import com.example.progr3ss.model.CreateHabitRequest
import com.example.progr3ss.model.HabitCategory
import com.example.progr3ss.model.HabitResponse
import com.example.progr3ss.network.RetrofitClient
import retrofit2.Response

class HabitRepository(context: Context) {

    private val api by lazy { RetrofitClient.getInstance(context) }

    suspend fun getCategories(): List<HabitCategory> {
        return api.getHabitCategories()
    }

    suspend fun createHabit(
        name: String,
        description: String?,
        categoryId: Long,
        goal: String
    ): Response<HabitResponse> {
        return api.createHabit(CreateHabitRequest(name, description, categoryId, goal))
    }

    suspend fun getHabits(): List<HabitResponse> {
        return api.getHabits()
    }
}