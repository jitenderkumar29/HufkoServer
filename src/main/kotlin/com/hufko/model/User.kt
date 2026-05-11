package com.hufko.model

import com.hufko.enums.Role
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val username: String,

    @Indexed(unique = true)
    val email: String,

    val password: String,

    val profile: Profile? = null,

    val roles: List<Role> = listOf(Role.USER),

    val isEnabled: Boolean = true,

    val isAccountNonLocked: Boolean = true,

    val refreshToken: String? = null,

    val refreshTokenExpiry: LocalDateTime? = null,

    val lastLogin: LocalDateTime? = null,

    @Version
    val version: Long = 0,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class Profile(
    val fullName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null
)