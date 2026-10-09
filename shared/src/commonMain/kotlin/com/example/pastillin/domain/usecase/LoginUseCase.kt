package com.example.pastillin.domain.usecase

import com.example.pastillin.domain.model.User
import com.example.pastillin.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, passwordHash: String): Result<User> {
        if (username.isBlank() || passwordHash.isBlank()) {
            return Result.failure(Exception("El usuario y la contraseña no pueden estar vacíos."))
        }
        return authRepository.login(username, passwordHash)
    }

    fun getPredefinedCredentials(): List<Pair<String, String>> {
        return authRepository.getAvailablePredefinedCredentials()
    }
}
