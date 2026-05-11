package com.hufko.dto

import com.hufko.enums.BannerType

data class BannerMetadata(
    val title: String,
    val description: String,
    val shortDescription: String,
    val resourceName: String,
    val priority: Int,
    val bannerType: BannerType,
    val superCategory: String,
    val category: String,
    val subCategory: String? = null,
    val clickUrl: String,
    val deepLink: String,
    val tags: List<String>
)