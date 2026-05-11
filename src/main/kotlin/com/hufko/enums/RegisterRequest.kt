package com.hufko.enums

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String, // ← make non-null
    val fullName: String,
    val phoneNumber: String?
)