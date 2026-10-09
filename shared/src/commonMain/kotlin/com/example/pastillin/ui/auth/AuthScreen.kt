package com.example.pastillin.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Login, 1 = Register

    // Login Form Fields
    var loginUser by remember { mutableStateOf("roberto") }
    var loginPass by remember { mutableStateOf("roberto123") }

    // Registration Form Fields (With Clinical Data)
    var regFullName by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regAge by remember { mutableStateOf("") }
    var regBloodType by remember { mutableStateOf("") }
    var regAllergies by remember { mutableStateOf("") }
    var regConditions by remember { mutableStateOf("") }
    var regEmergencyContact by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBF3FA))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Title & Tagline
                Text(
                    text = "💊 Pastillín",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D47A1)
                )
                Text(
                    text = "Gestión de Medicamentos y Registro Clínico",
                    fontSize = 14.sp,
                    color = Color(0xFF546E7A)
                )

                // Tab Row for Switching between Login & Registration
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFE3F2FD),
                    contentColor = Color(0xFF0D47A1)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            authViewModel.clearError()
                        },
                        text = { Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            authViewModel.clearError()
                        },
                        text = { Text("Crear Cuenta", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                    )
                }

                // Global Error Message Banner
                if (uiState is AuthUiState.Error) {
                    val errorMsg = (uiState as AuthUiState.Error).message
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }

                if (selectedTab == 0) {
                    // --- TAB 1: INICIAR SESIÓN ---
                    OutlinedTextField(
                        value = loginUser,
                        onValueChange = {
                            loginUser = it
                            authViewModel.clearError()
                        },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = loginPass,
                        onValueChange = {
                            loginPass = it
                            authViewModel.clearError()
                        },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = { authViewModel.login(loginUser, loginPass) },
                        enabled = uiState !is AuthUiState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("ENTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "Usuarios Demo Predefinidos (JSON Ktor):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF546E7A)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = (loginUser == "roberto"),
                            onClick = {
                                loginUser = "roberto"
                                loginPass = "roberto123"
                                authViewModel.clearError()
                            },
                            label = { Text("👴 Don Roberto") }
                        )
                        FilterChip(
                            selected = (loginUser == "maria"),
                            onClick = {
                                loginUser = "maria"
                                loginPass = "maria123"
                                authViewModel.clearError()
                            },
                            label = { Text("👵 Doña María") }
                        )
                    }

                } else {
                    // --- TAB 2: CREAR CUENTA (DATOS CLÍNICOS) ---
                    Text(
                        text = "Completa tus datos personales y clínicos:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0D47A1)
                    )

                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it; authViewModel.clearError() },
                        label = { Text("Nombre Completo *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regUsername,
                            onValueChange = { regUsername = it; authViewModel.clearError() },
                            label = { Text("Usuario *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it; authViewModel.clearError() },
                            label = { Text("Contraseña *") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regAge,
                            onValueChange = { regAge = it },
                            label = { Text("Edad (años)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = regBloodType,
                            onValueChange = { regBloodType = it },
                            label = { Text("Tipo de Sangre") },
                            singleLine = true,
                            placeholder = { Text("ej: O+") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = regAllergies,
                        onValueChange = { regAllergies = it },
                        label = { Text("Alergias Conocidas") },
                        placeholder = { Text("ej: Penicilina, Aspirina") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = regConditions,
                        onValueChange = { regConditions = it },
                        label = { Text("Padecimientos / Diagnósticos Clínicos") },
                        placeholder = { Text("ej: Hipertensión, Diabetes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = regEmergencyContact,
                        onValueChange = { regEmergencyContact = it },
                        label = { Text("Contacto de Emergencia") },
                        placeholder = { Text("ej: 555-1234 (Hijo/Familiar)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            authViewModel.register(
                                fullName = regFullName,
                                username = regUsername,
                                passwordHash = regPassword,
                                age = regAge,
                                bloodType = regBloodType,
                                allergies = regAllergies,
                                medicalConditions = regConditions,
                                emergencyContact = regEmergencyContact
                            )
                        },
                        enabled = uiState !is AuthUiState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Green button for account creation
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("CREAR MI CUENTA Y CONTINUAR", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
