package com.example.progr3ss.model

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class AuthResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("user")
    val user: User,
    @SerializedName("tokens")
    val tokens: Tokens
)

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("auth_provider")
    val authProvider: String?,
    @SerializedName("profile")
    val profile: Profile
)

data class Profile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("email")
    val email: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    @SerializedName("profileImageBase64")
    val profileImageBase64: String?,
    @SerializedName("coverImageUrl")
    val coverImageUrl: String?,
    @SerializedName("fcmToken")
    val fcmToken: String?,
    @SerializedName("preferences")
    val preferences: Map<String, Any>?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class Tokens(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class SignUpResponse(
    @SerializedName("message")
    val message: String?
)