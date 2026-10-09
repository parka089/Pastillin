package com.example.pastillin.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val age: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val emergencyContact: String = ""
)

@Serializable
data class LoginRequest(
    val username: String,
    val passwordHash: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val age: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val emergencyContact: String = ""
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val user: UserDto? = null,
    val errorMessage: String? = null
)
