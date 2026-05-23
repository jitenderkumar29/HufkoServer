package com.hufko.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

data class RestaurantJsonData(
    val id: Int,
    val category: List<String> = listOf("ALL"),
    val categoryRest: String,
    val outlet: String,
    val title: String,
    val restaurantName: String,
    val address: AddressJsonData? = null,
    val description: String,
    val imageUrl: String,
    val thumbnailImageRes: String,
    @JsonProperty("TopRatedImageRes")
    val topRatedImageRes: String,
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
    val operatingHours: List<OperatingHourJsonData> = emptyList(),
    val topRated: Boolean = false,
    val recommended: Boolean = false,
    val featured: Boolean = false,
    val calories: String = "",
    val protein: String = "",
    val isHighProtein: Boolean = false,
    val isVeg: Boolean = false,
    val isWishlisted: Boolean = false,
    val highlyReordered: String = "",
    val reorderedQuantity: String = "",
    // Changed from Map<String, Any>? to JsonNode? to handle both {} and String
    val filtersJson: JsonNode? = null,
    val offer: JsonNode? = null,
    val isActive: Boolean = true,
    val isVerified: Boolean = false,
)

data class AddressJsonData(
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

data class OperatingHourJsonData(
    val day: String,
    val openTime: String,
    val closeTime: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val count: Int? = null,
    val page: Int? = null,
    val totalPages: Int? = null,
    val totalElements: Long? = null
)

data class FilterRequest(
    val category: List<String>? = null,
    val topRated: Boolean? = null,
    val recommended: Boolean? = null,
    val featured: Boolean? = null,
    val minRating: String? = null,
    val outlet: String? = null,
    val cuisineType: String? = null,
    val pureVeg: Boolean? = null,
    val searchTerm: String? = null
)