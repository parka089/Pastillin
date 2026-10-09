package com.example.pastillin.data.datasource

import com.example.pastillin.data.model.UserDto
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
            },
            {
                "id": "u2",
                "username": "maria",
                "passwordHash": "maria123",
                "fullName": "Doña María López",
                "age": "68",
                "bloodType": "A+",
                "allergies": "Aspirina",
                "medicalConditions": "Artritis Reumatoide",
                "emergencyContact": "555-0482 (Hija - Ana)"
            }
        ]
    """.trimIndent()

    private val jsonFormatter = Json {
        ignoreUnknownKeys = true
    }

    private val mutableUsersList: MutableList<UserDto> by lazy {
        jsonFormatter.decodeFromString<List<UserDto>>(initialUsersJson).toMutableList()
    }

    fun getUsers(): List<UserDto> {
        return mutableUsersList
    }

    fun addUser(user: UserDto) {
        mutableUsersList.add(user)
    }
}
