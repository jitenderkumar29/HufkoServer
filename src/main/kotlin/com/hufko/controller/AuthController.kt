package com.hufko.controller

import com.hufko.dto.*
import com.hufko.security.JwtTokenProvider
import com.hufko.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @GetMapping("/test")
    fun test(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(
            mapOf(
                "status" to "OK",
                "message" to "Auth controller is working",
                "timestamp" to System.currentTimeMillis().toString()
            )
        )
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(loginRequest)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(registerRequest)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        val response = authService.refreshToken(refreshTokenRequest.refreshToken)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String): ResponseEntity<Map<String, String>> {
        authService.logout(token.substring(7))
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
}