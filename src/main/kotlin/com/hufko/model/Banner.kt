package com.hufko.model

import com.hufko.enums.BannerType
import com.hufko.enums.ImageStatus
import com.hufko.enums.Role
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "banners")
@CompoundIndex(name = "category_priority_idx", def = "{'categoryId': 1, 'priority': -1}")
@CompoundIndex(name = "active_type_idx", def = "{'isActive': 1, 'bannerType': 1}")
data class Banner(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val bannerId: String,

    @Indexed
    val title: String,

    val description: String,
    val shortDescription: String? = null,

    // Category hierarchy
    val superCategory: String,
    val category: String,
    val subCategory: String? = null,

    // Image data
    val imageUrl: String,
    val thumbnailUrl: String,
    val mobileImageUrl: String? = null,
    val tabletImageUrl: String? = null,
    val imageMetadata: ImageMetadata,

    // Android resource mapping
    val drawableResourceId: Int? = null,
    val resourceName: String? = null,
    val resourcePath: String? = null,

    // Banner configuration
    val bannerType: BannerType,
    val priority: Int = 0,
    val isActive: Boolean = true,
    val status: ImageStatus = ImageStatus.ACTIVE,

    // Targeting
    val targetRoles: List<Role>? = null,
    val targetLocations: List<String>? = null,
    val targetDevices: List<String>? = null,

    // Scheduling
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,

    // Links & Actions
    val clickUrl: String? = null,
    val deepLink: String? = null,
    val actionType: String? = null,
    val actionData: Map<String, Any>? = null,

    // Analytics
    val clickCount: Long = 0,
    val viewCount: Long = 0,
    val ctr: Double = 0.0,

    // Metadata
    val tags: List<String> = emptyList(),
    val metadata: Map<String, Any> = emptyMap(),

    @Version
    val version: Long = 0,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    val createdBy: String? = null,
    val updatedBy: String? = null
)

data class SuperCategory(
    val id: String,
    val name: String,
    val code: String,
    val description: String? = null,
    val iconUrl: String? = null
)

data class Category(
    val id: String,
    val name: String,
    val code: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val displayOrder: Int = 0
)

data class SubCategory(
    val id: String,
    val name: String,
    val code: String,
    val description: String? = null,
    val parentCategoryId: String
)

data class ImageMetadata(
    val originalFileName: String,
    val fileSize: Long,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val aspectRatio: Double,
    val colors: List<String> = emptyList(),
    val dominantColor: String? = null,
    val altText: String? = null,
    val title: String? = null,
    val compressionRatio: Double = 0.0,
    val hash: String? = null
)