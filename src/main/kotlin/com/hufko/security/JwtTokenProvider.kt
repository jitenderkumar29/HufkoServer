package com.hufko.security

import com.hufko.service.CustomUserDetails
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider {

    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    @Value("\${app.jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${app.jwt.expiration-in-ms}")
    private val jwtExpirationInMs: Long = 86400000

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun generateToken(username: String, roles: List<String>): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)

        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun generateToken(authentication: Authentication): String {
        // Handle both UserDetails and String principal
        val username = when (val principal = authentication.principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> principal.toString()
        }

        // Get roles from authentication and filter out nulls
        val roles = authentication.authorities
            .map { it.authority }
            .filterNotNull()
            .toList()

        return generateToken(username, roles)
    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            getClaimsFromToken(token).subject
        } catch (ex: Exception) {
            logger.error("Could not get username from token", ex)
            null
        }
    }

    fun getRolesFromToken(token: String): List<String> {
        return try {
            val claims = getClaimsFromToken(token)
            @Suppress("UNCHECKED_CAST")
            claims["roles"] as? List<String> ?: emptyList()
        } catch (ex: Exception) {
            logger.error("Could not get roles from token", ex)
            emptyList()
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaimsFromToken(token)
            true
        } catch (ex: SecurityException) {
            logger.error("Invalid JWT signature: {}", ex.message)
            false
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", ex.message)
            false
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token: {}", ex.message)
            false
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token: {}", ex.message)
            false
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", ex.message)
            false
        }
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun getExpirationDateFromToken(token: String): Date? {
        return try {
            getClaimsFromToken(token).expiration
        } catch (ex: Exception) {
            null
        }
    }

    fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token) ?: return true
        return expiration.before(Date())
    }
}