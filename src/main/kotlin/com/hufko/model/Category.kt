package com.hufko.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "categories")
data class CategoryHierarchy(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val code: String,

    val name: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val bannerImageUrl: String? = null,

    val parentId: String? = null,
    val level: Int = 0, // 0: Super, 1: Main, 2: Sub
    val path: String, // e.g., "/food/all_food/burgers"

    val displayOrder: Int = 0,
    val isActive: Boolean = true,

    val metadata: Map<String, Any> = emptyMap(),

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    val updatedAt: LocalDateTime = LocalDateTime.now()
)