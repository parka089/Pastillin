package com.example.pastillin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform