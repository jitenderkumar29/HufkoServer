package com.hufko.loader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hufko.dto.RestaurantJsonData
import com.hufko.model.Address
import com.hufko.model.OperatingHour
import com.hufko.model.Restaurant
import com.hufko.repository.RestaurantRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
@Order(2)
class RestaurantDataLoader(
    private val restaurantRepository: RestaurantRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (restaurantRepository.count() == 0L) {
            println("📦 Loading restaurants from JSON file...")
            loadRestaurantsFromJson()
        } else {
            println("✅ Database already contains ${restaurantRepository.count()} restaurants")
        }
    }

    private fun loadRestaurantsFromJson() {
        val objectMapper = jacksonObjectMapper()

        try {
            val inputStream = javaClass.getResourceAsStream("/restaurant_data.json")

            if (inputStream == null) {
                println("❌ restaurant_data.json not found in resources")
                return
            }

            val restaurantDataList: List<RestaurantJsonData> =
                objectMapper.readValue(inputStream)

            println("📄 Found ${restaurantDataList.size} restaurants in JSON")

            val restaurants = restaurantDataList.map { data ->
                createRestaurantFromJson(data)
            }

            restaurantRepository.saveAll(restaurants)

            println("✅ Successfully loaded ${restaurants.size} restaurants from JSON")

            // Print summary
            println("\n📊 Restaurant Summary:")
            restaurants.forEach { restaurant ->
                println("   • ${restaurant.title} (${restaurant.restaurantName})")
                println("     Category: ${restaurant.category}")
                println("     Rating: ${restaurant.rating} | Top Rated: ${restaurant.topRated}")
                println("     Price: ${restaurant.priceAvg} | Distance: ${restaurant.distance}")
                println("     Outlet: ${restaurant.outlet}")
            }

        } catch (e: Exception) {
            println("❌ Error loading restaurant data: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun createRestaurantFromJson(data: RestaurantJsonData): Restaurant {
        // Convert JsonNode to Map if needed, otherwise empty map
        val filtersMap = data.filtersJson?.let { node ->
            if (node.isObject) {
                objectMapper.readValue(node.traverse(), Map::class.java) as? Map<String, Any> ?: emptyMap()
            } else {
                emptyMap()
            }
        } ?: emptyMap()

        val offerMap = data.offer?.let { node ->
            if (node.isObject) {
                objectMapper.readValue(node.traverse(), Map::class.java) as? Map<String, Any> ?: emptyMap()
            } else {
                emptyMap()
            }
        } ?: emptyMap()

        return Restaurant(
            restaurantId = generateRestaurantId(),
            category = data.category,
            categoryRest = data.categoryRest,
            outlet = data.outlet,
            title = data.title,
            restaurantName = data.restaurantName,
            address = data.address?.let { addr ->
                Address(
                    houseNo = addr.houseNo,
                    apartment = addr.apartment,
                    street = addr.street,
                    landmark = addr.landmark,
                    city = addr.city,
                    district = addr.district,
                    state = addr.state,
                    country = addr.country,
                    pinCode = addr.pinCode,
                    addressType = addr.addressType
                )
            },
            description = data.description,
            imageUrl = data.imageUrl,
            thumbnailImageRes = data.thumbnailImageRes,
            topRatedImageRes = data.topRatedImageRes,
            videoUrls = data.videoUrls,
            galleryImages = data.galleryImages,
            priceAvg = data.priceAvg,
            discountAvg = data.discountAvg,
            discountAmountAvg = data.discountAmountAvg,
            originalPriceAvg = data.originalPriceAvg,
            minOrderValue = data.minOrderValue,
            deliveryFee = data.deliveryFee,
            rating = data.rating,
            totalRatings = data.totalRatings,
            ratingDescription = data.ratingDescription,
            deliveryTime = data.deliveryTime,
            distance = data.distance,
            premium = data.premium,
            acceptingOrders = data.acceptingOrders,
            acceptingOrdersMsg = data.acceptingOrdersMsg,
            isOpen = data.isOpen,
            nextOpenTime = data.nextOpenTime,
            isCurrentlyAcceptingOrders = data.isCurrentlyAcceptingOrders,
            cuisineType = data.cuisineType,
            isPureVeg = data.isPureVeg,
            hasAlcohol = data.hasAlcohol,
            contactPhone = data.contactPhone,
            contactEmail = data.contactEmail,
            website = data.website,
            latitude = data.latitude,
            longitude = data.longitude,
            landmark = data.landmark,
            isFavorite = data.isFavorite,
            isSponsored = data.isSponsored,
            isBookmarked = data.isBookmarked,
            offerLabel = data.offerLabel,
            couponCode = data.couponCode,
            freeDelivery = data.freeDelivery,
            hasParking = data.hasParking,
            hasWifi = data.hasWifi,
            hasOutdoorSeating = data.hasOutdoorSeating,
            hasHomeDelivery = data.hasHomeDelivery,
            hasDineIn = data.hasDineIn,
            hasTakeaway = data.hasTakeaway,
            operatingHours = data.operatingHours.map { oh ->
                OperatingHour(
                    day = oh.day,
                    openTime = oh.openTime,
                    closeTime = oh.closeTime
                )
            },
            topRated = data.topRated,
            recommended = data.recommended,
            featured = data.featured,
            calories = data.calories,
            protein = data.protein,
            isHighProtein = data.isHighProtein,
            isVeg = data.isVeg,
            isWishlisted = data.isWishlisted,
            highlyReordered = data.highlyReordered,
            reorderedQuantity = data.reorderedQuantity,
            filtersJson = filtersMap,
            offer = offerMap,
            isActive = true,
            isVerified = data.isVerified,
            version = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            createdBy = "system",
            updatedBy = "system"
        )
    }

    private fun generateRestaurantId(): String {
        return "REST_${System.currentTimeMillis()}_${UUID.randomUUID().toString().substring(0, 8)}"
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()
    }
}