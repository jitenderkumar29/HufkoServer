package com.hufko.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @NotBlank
    val username: String,

    @NotBlank
    val password: String
)

data class RegisterRequest(
    @NotBlank
    @Size(min = 3, max = 20)
    val username: String,

    @NotBlank
    @Email
    val email: String,

    @NotBlank
    @Size(min = 6, max = 40)
    val password: String,

    @NotBlank
    val fullName: String,

    val phoneNumber: String? = null
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserDTO
)

data class UserDTO(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String?,
    val roles: List<String>
)

data class RefreshTokenRequest(
    @NotBlank
    val refreshToken: String
)