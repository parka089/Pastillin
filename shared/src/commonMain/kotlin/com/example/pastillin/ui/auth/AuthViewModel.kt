package com.example.pastillin.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pastillin.data.model.RegisterRequest
import com.example.pastillin.data.repository.AuthRepositoryImpl
import com.example.pastillin.domain.model.User
import com.example.pastillin.domain.usecase.LoginUseCase
import com.example.pastillin.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase = LoginUseCase(AuthRepositoryImpl()),
    private val registerUseCase: RegisterUseCase = RegisterUseCase(AuthRepositoryImpl())
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val predefinedCredentials: List<Pair<String, String>>
        get() = loginUseCase.getPredefinedCredentials()

    fun login(username: String, passwordHash: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginUseCase(username, passwordHash)
            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun register(
        fullName: String,
        username: String,
        passwordHash: String,
        age: String,
        bloodType: String,
        allergies: String,
        medicalConditions: String,
        emergencyContact: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val req = RegisterRequest(
                fullName = fullName,
                username = username,
                passwordHash = passwordHash,
                age = age,
                bloodType = bloodType,
                allergies = allergies,
                medicalConditions = medicalConditions,
                emergencyContact = emergencyContact
            )
            val result = registerUseCase(req)
            result.onSuccess { user ->
                _currentUser.value = user
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(error.message ?: "Error al registrar cuenta")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
