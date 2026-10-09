package com.example.pastillin.data.repository

import com.example.pastillin.data.datasource.LocalUserDataSource
import com.example.pastillin.data.model.RegisterRequest
import com.example.pastillin.data.network.KtorAuthApiClient
import com.example.pastillin.domain.model.User
import com.example.pastillin.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiClient: KtorAuthApiClient = KtorAuthApiClient()
) : AuthRepository {

    override suspend fun login(username: String, passwordHash: String): Result<User> {
        val response = apiClient.performLogin(username, passwordHash)
        return if (response.success && response.user != null) {
            val dto = response.user
            val user = User(
                id = dto.id,
                username = dto.username,
                fullName = dto.fullName,
                age = dto.age,
                bloodType = dto.bloodType,
                allergies = dto.allergies,
                medicalConditions = dto.medicalConditions,
                emergencyContact = dto.emergencyContact
            )
            Result.success(user)
        } else {
            Result.failure(Exception(response.errorMessage ?: "Error al autenticar"))
        }
    }

    override suspend fun register(request: RegisterRequest): Result<User> {
        val response = apiClient.performRegister(request)
        return if (response.success && response.user != null) {
            val dto = response.user
            val user = User(
                id = dto.id,
                username = dto.username,
                fullName = dto.fullName,
                age = dto.age,
                bloodType = dto.bloodType,
                allergies = dto.allergies,
                medicalConditions = dto.medicalConditions,
                emergencyContact = dto.emergencyContact
            )
            Result.success(user)
        } else {
            Result.failure(Exception(response.errorMessage ?: "Error al registrar la cuenta"))
        }
    }

    override fun getAvailablePredefinedCredentials(): List<Pair<String, String>> {
        return LocalUserDataSource.getUsers().map {
            Pair(it.username, it.passwordHash)
        }
    }
}
