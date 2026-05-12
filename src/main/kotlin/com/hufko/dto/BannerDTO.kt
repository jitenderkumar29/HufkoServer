package com.hufko.dto

import com.hufko.enums.BannerType
import java.time.LocalDateTime

data class BannerDataDTO(
    val title: String,
    val description: String,
    val shortDescription: String? = null,
    val resourceName: String,
    val priority: Int,
    val bannerType: BannerType,

    // UPDATED
    val superCategory: String,
    val category: String,
    val subCategory: String? = null,

    val imageUrl: String,
    val thumbnailUrl: String,
    val mobileImageUrl: String? = null,
    val tabletImageUrl: String? = null,
    val clickUrl: String? = null,
    val deepLink: String? = null,
    val tags: List<String> = emptyList()
)

data class BannerCreateDTO(
    val title: String,
    val description: String,
    val shortDescription: String? = null,

    // IDs
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
    val tags: List<String> = emptyList()
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

    // UPDATED
    val superCategory: String,
    val category: String,
    val subCategory: String? = null,

    val imageUrl: String,
    val thumbnailUrl: String,
    val mobileImageUrl: String? = null,
    val tabletImageUrl: String? = null,

    val bannerType: BannerType,
    val priority: Int,
    val isActive: Boolean,

    val resourceName: String? = null,
    val clickUrl: String? = null,
    val deepLink: String? = null,

    val tags: List<String> = emptyList(),

    val viewCount: Long = 0,
    val clickCount: Long = 0,
    val ctr: Double = 0.0
)

data class BannerJsonData(
    val title: String,
    val description: String,
    val shortDescription: String? = null,
    val resourceName: String,
    val priority: Int,
    val bannerType: BannerType,

    // UPDATED
    val superCategory: String,
    val category: String,
    val subCategory: String? = null,

    val imageUrl: String,
    val thumbnailUrl: String,
    val mobileImageUrl: String? = null,
    val tabletImageUrl: String? = null,
    val clickUrl: String? = null,
    val deepLink: String? = null,
    val tags: List<String> = emptyList()
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