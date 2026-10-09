package com.example.pastillin.domain.model

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val age: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val emergencyContact: String = ""
)
