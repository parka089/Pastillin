package com.example.pastillin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pastillin.ui.auth.AuthScreen
import com.example.pastillin.ui.auth.AuthViewModel
import com.example.pastillin.ui.home.MainMedicationScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel { AuthViewModel() }
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    if (currentUser == null) {
        AuthScreen(
            authViewModel = authViewModel,
            onAuthSuccess = {
                // User authenticated or account created successfully
            }
        )
    } else {
        MainMedicationScreen(
            user = currentUser!!,
            onLogout = { authViewModel.logout() }
        )
    }
}
