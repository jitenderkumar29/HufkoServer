package com.hufko.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "restaurants")
data class Restaurant(
    @Id
    val id: String? = null,

    @Indexed
    val restaurantId: String,

    @Indexed
    val category: List<String> = listOf("ALL"),

    val categoryRest: String,
    val outlet: String,
    val title: String,
    val restaurantName: String,
    val description: String,
    val address: Address? = null,
    val imageUrl: String? = null,
    val thumbnailImageRes: String? = null,
    val topRatedImageRes: String? = null,
    val videoUrls: List<String> = emptyList(),
    val galleryImages: List<Int> = emptyList(),
    val priceAvg: String,
    val discountAvg: String? = null,
    val discountAmountAvg: String? = null,
    val originalPriceAvg: String? = null,
    val minOrderValue: String = "₹149",
    val deliveryFee: String = "₹0",
    val rating: String,
    val totalRatings: Int = 0,
    val ratingDescription: String? = null,
    val deliveryTime: String,
    val distance: String,
    val premium: String = "premium",
    val acceptingOrders: Boolean = true,
    val acceptingOrdersMsg: String = "",
    val isOpen: Boolean = true,
    val nextOpenTime: String = "",
    val isCurrentlyAcceptingOrders: Boolean = true,
    val cuisineType: List<String> = emptyList(),
    val isPureVeg: Boolean = false,
    val hasAlcohol: Boolean = false,
    val contactPhone: String = "",
    val contactEmail: String = "",
    val website: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val landmark: String = "",
    val isFavorite: Boolean = false,
    val isSponsored: Boolean = false,
    val isBookmarked: Boolean = false,
    val offerLabel: String = "",
    val couponCode: String = "",
    val freeDelivery: Boolean = true,
    val hasParking: Boolean = false,
    val hasWifi: Boolean = false,
    val hasOutdoorSeating: Boolean = false,
    val hasHomeDelivery: Boolean = true,
    val hasDineIn: Boolean = false,
    val hasTakeaway: Boolean = true,
    val operatingHours: List<OperatingHour> = emptyList(),
    val calories: String = "",
    val protein: String = "",
    val isHighProtein: Boolean = false,
    val isVeg: Boolean = false,
    val topRated: Boolean = false,
    val recommended: Boolean = false,
    val featured: Boolean = false,
    val isWishlisted: Boolean = false,
    val highlyReordered: String = "",
    val reorderedQuantity: String = "",
    val filtersJson: Map<String, Any> = emptyMap(),
    val offer: Map<String, Any> = emptyMap(),
    val isActive: Boolean = true,
    val isVerified: Boolean = false,

    @Version
    val version: Long = 0,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    val createdBy: String = "system",
    val updatedBy: String = "system"
)

data class Address(
    val houseNo: String = "",
    val apartment: String = "",
    val street: String = "",
    val landmark: String = "",
    val city: String = "",
    val district: String = "",
    val state: String = "",
    val country: String = "India",
    val pinCode: String = "",
    val addressType: String = "Commercial"
)

data class OperatingHour(
    val day: String,
    val openTime: String,
    val closeTime: String
)