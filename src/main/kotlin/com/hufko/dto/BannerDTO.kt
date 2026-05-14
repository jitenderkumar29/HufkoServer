package com.hufko.dto

import com.hufko.enums.BannerType
import java.time.LocalDateTime

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BannerDataDTO(
    val title: String,
    val description: String,
    val shortDescription: String? = null,
    val resourceName: String,
    val priority: Int,
    val bannerType: BannerType,
    val superCategory: String,
    val category: String,
    val subCategory: String? = null,
    val imageUrl: String,
    val thumbnailUrl: String,
    val mobileImageUrl: String? = null,
    val tabletImageUrl: String? = null,
    val clickUrl: String? = null,
    val deepLink: String? = null,
    val tags: List<String>? = null,
    // ========== DIET FOOD SPECIFIC FIELDS ==========
    val price: String? = null,
    val restaurantName: String? = null,
    val rating: String? = null,
    val deliveryTime: String? = null,
    val distance: String? = null,
    val discount: String? = null,
    val discountAmount: String? = null,
    val address: String? = null,
    val calories: String? = null,
    val protein: String? = null,
    val isHighProtein: Boolean = false
)

data class BannerCreateDTO(
    val title: String,
    val description: String,
    val shortDescription: String? = null,

    val superCategoryId: String,
    val categoryId: String,
    val subCategoryId: String? = null,

    val bannerType: BannerType,
    val priority: Int = 0,

    val resourceName: String? = null,

    val clickUrl: String? = null,
    val deepLink: String? = null,

    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,

    val tags: List<String> = emptyList(),

    // ========== DIET FOOD FIELDS ==========
    val price: String? = null,
    val restaurantName: String? = null,
    val rating: String? = null,
    val deliveryTime: String? = null,
    val distance: String? = null,
    val discount: String? = null,
    val discountAmount: String? = null,
    val address: String? = null,
    val calories: String? = null,
    val protein: String? = null,
    val isHighProtein: Boolean = false
)

data class BannerUpdateDTO(
    val title: String? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val bannerType: BannerType? = null,
    val priority: Int? = null,
    val isActive: Boolean? = null,
    val clickUrl: String? = null,
    val deepLink: String? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)

data class BannerDTO(
    val id: String? = null,
    val bannerId: String,
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

    // Banner configuration
    val bannerType: BannerType,
    val priority: Int = 0,
    val isActive: Boolean = true,

    // Resource mapping
    val resourceName: String? = null,

    // Links & Actions
    val clickUrl: String? = null,
    val deepLink: String? = null,

    // Analytics
    val clickCount: Long = 0,
    val viewCount: Long = 0,
    val ctr: Double = 0.0,

    // Metadata
    val tags: List<String> = emptyList(),

    // ========== DIET FOOD SPECIFIC FIELDS - ADD THESE ==========
    val price: String? = null,
    val restaurantName: String? = null,
    val rating: String? = null,
    val deliveryTime: String? = null,
    val distance: String? = null,
    val discount: String? = null,
    val discountAmount: String? = null,
    val address: String? = null,
    val calories: String? = null,
    val protein: String? = null,
    val isHighProtein: Boolean = false,
    // ============================================================

    // Timestamps
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

data class BannerJsonData(
    val title: String,
    val description: String,
    val shortDescription: String? = null,
    val resourceName: String,
    val priority: Int,
    val bannerType: BannerType,
    val superCategory: String,
    val category: String,
    val subCategory: String? = null,
    val imageUrl: String,
    val thumbnailUrl: String,
    val mobileImageUrl: String,
    val tabletImageUrl: String? = null,
    val clickUrl: String? = null,
    val deepLink: String? = null,
    val tags: List<String>? = null,
    // Diet food specific fields
    val price: String? = null,
    val restaurantName: String? = null,
    val rating: String? = null,
    val deliveryTime: String? = null,
    val distance: String? = null,
    val discount: String? = null,
    val discountAmount: String? = null,
    val address: String? = null,
    val calories: String? = null,
    val protein: String? = null,
    val isHighProtein: Boolean = false
)

data class BannerResponseDTO(
    val banners: List<BannerDTO>,
    val pagination: PaginationInfo
)

data class PaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Long,
    val pageSize: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)