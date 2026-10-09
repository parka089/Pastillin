package com.example.pastillin.domain.repository

import com.example.pastillin.data.model.RegisterRequest
import com.example.pastillin.domain.model.User

interface AuthRepository {
    suspend fun login(username: String, passwordHash: String): Result<User>
    suspend fun register(request: RegisterRequest): Result<User>
    fun getAvailablePredefinedCredentials(): List<Pair<String, String>>
}
