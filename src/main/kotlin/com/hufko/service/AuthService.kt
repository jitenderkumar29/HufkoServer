package com.hufko.service

import com.hufko.dto.*
import com.hufko.enums.Role
import com.hufko.model.Profile
import com.hufko.model.User
import com.hufko.repository.UserRepository
import com.hufko.security.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime
import java.util.*

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

//    @GetMapping("/test")
//    fun test(): ResponseEntity<Map<String, String>> {
//        return ResponseEntity.ok(mapOf("status" to "Server is running"))
//    }

    fun login(loginRequest: LoginRequest): AuthResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )

        val user = userRepository.findByUsername(loginRequest.username)
            .orElseThrow { RuntimeException("User not found") }

        val updatedUser = user.copy(
            lastLogin = LocalDateTime.now(),
            refreshToken = generateRefreshToken(),
            refreshTokenExpiry = LocalDateTime.now().plusDays(7)
        )
        userRepository.save(updatedUser)

        val accessToken = jwtTokenProvider.generateToken(authentication)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = updatedUser.refreshToken ?: throw RuntimeException("Refresh token is null"),
            expiresIn = 86400000,
            user = UserDTO(
                id = updatedUser.id ?: throw RuntimeException("User ID is null"),
                username = updatedUser.username,
                email = updatedUser.email,
                fullName = updatedUser.profile?.fullName ?: "",
                avatarUrl = updatedUser.profile?.avatarUrl,
                roles = updatedUser.roles.map { it.name }
            )
        )
    }

    fun register(registerRequest: RegisterRequest): AuthResponse {
        println("register user....................................................")
        // Extract values with proper null checks and assign to non-nullable variables
        val username = registerRequest.username ?: throw RuntimeException("Username is required")
        val email = registerRequest.email ?: throw RuntimeException("Email is required")
        val password = registerRequest.password ?: throw RuntimeException("Password is required")
        val fullName = registerRequest.fullName ?: throw RuntimeException("Full name is required")

        if (userRepository.existsByUsername(username)) {
            throw RuntimeException("Username is already taken")
        }

        if (userRepository.existsByEmail(email)) {
            throw RuntimeException("Email is already in use")
        }

        val refreshToken = generateRefreshToken()

        val encodedPassword = passwordEncoder.encode(password)
            ?: throw RuntimeException("Password encoding failed")

        val user = User(
            username = username,
            email = email,
            password = encodedPassword,
            profile = Profile(
                fullName = fullName,
                firstName = fullName.split(" ").firstOrNull() ?: "",
                lastName = fullName.split(" ").drop(1).joinToString(" "),
                phoneNumber = registerRequest.phoneNumber
            ),
            roles = listOf(Role.USER),
            refreshToken = refreshToken,
            refreshTokenExpiry = LocalDateTime.now().plusDays(7)
        )

        val savedUser = userRepository.save(user)

        val authentication = UsernamePasswordAuthenticationToken(username, password)
        val accessToken = jwtTokenProvider.generateToken(authentication)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = 86400000,
            user = UserDTO(
                id = savedUser.id ?: throw RuntimeException("User ID is null"),
                username = savedUser.username,
                email = savedUser.email,
                fullName = savedUser.profile?.fullName ?: "",
                avatarUrl = savedUser.profile?.avatarUrl,
                roles = savedUser.roles.map { it.name }
            )
        )
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        val user = userRepository.findByRefreshToken(refreshToken)
            .orElseThrow { RuntimeException("Invalid refresh token") }

        val expiry = user.refreshTokenExpiry
        if (expiry != null && expiry.isBefore(LocalDateTime.now())) {
            throw RuntimeException("Refresh token expired")
        }

        val authentication = UsernamePasswordAuthenticationToken(user.username, null)
        val newAccessToken = jwtTokenProvider.generateToken(authentication)

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken,
            expiresIn = 86400000,
            user = UserDTO(
                id = user.id ?: throw RuntimeException("User ID is null"),
                username = user.username,
                email = user.email,
                fullName = user.profile?.fullName ?: "",
                avatarUrl = user.profile?.avatarUrl,
                roles = user.roles.map { it.name }
            )
        )
    }

    fun logout(token: String) {
        println("Logging out token: $token")
    }

    private fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }
}