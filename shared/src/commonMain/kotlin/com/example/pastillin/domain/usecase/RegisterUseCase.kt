package com.example.pastillin.domain.usecase

import com.example.pastillin.data.model.RegisterRequest
import com.example.pastillin.domain.model.User
import com.example.pastillin.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Result<User> {
        if (request.fullName.isBlank()) {
            return Result.failure(Exception("Por favor ingrese su nombre completo."))
        }
        if (request.username.isBlank() || request.passwordHash.isBlank()) {
            return Result.failure(Exception("El usuario y la contraseña son obligatorios."))
        }
        return authRepository.register(request)
    }
}
