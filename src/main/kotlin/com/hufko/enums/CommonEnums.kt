//package org.example.com.hufko.enums

package com.hufko.enums

enum class Role {
    ADMIN,
    USER,
    PREMIUM_USER,
    MODERATOR,
    GUEST
}

enum class BannerType {
    HOME_PAGE,
    CATEGORY_PAGE,
    PROMOTIONAL,
    SEASONAL,
    FLASH_SALE,
    SPECIAL_OFFER,
    POPULAR,
    TRENDING,
    RECOMMENDED
}

enum class FolderType {
    ADMIN,
    USER,
    PREMIUM,
    PUBLIC,
    TEMP
}

enum class ImageStatus {
    ACTIVE,
    INACTIVE,
    ARCHIVED,
    DELETED
}

enum class BannerPriority {
    HIGHEST(1),
    HIGH(2),
    MEDIUM(3),
    LOW(4),
    LOWEST(5);

    val value: Int
    constructor(value: Int) {
        this.value = value
    }
}