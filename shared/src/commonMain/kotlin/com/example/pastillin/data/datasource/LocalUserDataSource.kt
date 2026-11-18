package com.example.pastillin.data.datasource

import com.example.pastillin.data.model.UserDto
import com.example.pastillin.ui.home.MedicationItem
import kotlinx.serialization.json.Json

object LocalUserDataSource {

    val initialUsersJson = """
        [
            {
                "id": "u1",
                "username": "roberto",
                "passwordHash": "roberto123",
                "fullName": "Don Roberto Pérez",
                "age": "72",
                "bloodType": "O+",
                "allergies": "Penicilina, Sulfa",
                "medicalConditions": "Hipertensión Arterial, Diabetes Tipo 2",
                "emergencyContact": "555-0192 (Hijo - Carlos)"
            }
        ]
    """.trimIndent()

    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
    }

    private val mutableUsersList: MutableList<UserDto> by lazy {
        jsonFormatter.decodeFromString<List<UserDto>>(initialUsersJson).toMutableList()
    }

    val persistentMedications: MutableList<MedicationItem> = mutableListOf(
        MedicationItem("m1", "Paracetamol", "1 Pastilla (500mg)", "08:00 AM", "Cada 8 horas", "7 Días", "💊", photoCaptured = true),
        MedicationItem("m2", "Losartán", "1 Pastilla (50mg)", "10:00 AM", "Hora fija (Diaria)", "30 Días", "⚪", photoCaptured = true),
        MedicationItem("m3", "Omeprazol", "1 Cápsula (20mg)", "08:00 PM", "Cada 12 horas", "14 Días", "💊", photoCaptured = true)
    )

    fun getUsers(): List<UserDto> {
        return mutableUsersList
    }

    fun addUser(user: UserDto) {
        mutableUsersList.add(user)
    }

    fun getMedications(): List<MedicationItem> {
        return persistentMedications.toList()
    }

    fun addMedication(item: MedicationItem) {
        persistentMedications.add(item)
    }

    fun deleteMedication(id: String) {
        persistentMedications.removeAll { it.id == id }
    }
}
