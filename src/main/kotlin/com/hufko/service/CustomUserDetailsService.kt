package com.hufko.service

import com.hufko.enums.Role
import com.hufko.model.User as HufkoUser
import com.hufko.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        // Try to find by email first, then by username
        val user = userRepository.findByEmail(username)
            .orElseGet {
                userRepository.findByUsername(username)
                    .orElseThrow { UsernameNotFoundException("User not found with username/email: $username") }
            }

        return CustomUserDetails(user)
    }
}

class CustomUserDetails(
    private val user: HufkoUser
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.roles.map { SimpleGrantedAuthority("ROLE_${it.name}") }.toMutableList()
    }

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email // Use email as username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = user.isAccountNonLocked

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.isEnabled

    // Helper method to get userId
    fun getUserId(): String = user.id ?: ""

    // Helper method to get roles as enum
    fun getRoles(): List<Role> = user.roles
}