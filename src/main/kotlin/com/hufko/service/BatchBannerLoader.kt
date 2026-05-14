package com.hufko.service

import com.hufko.enums.BannerType
import com.hufko.enums.ImageStatus
import com.hufko.model.*
import com.hufko.repository.BannerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime
import java.util.*

@Component
@Order(2)  // Run after DataInitializer
class BatchBannerLoader(
    private val bannerRepository: BannerRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (bannerRepository.count() == 0L) {
            println("📦 Loading banners from assets folder...")
            loadAllBannersFromAssets()
        } else {
            println("✅ Database already contains ${bannerRepository.count()} banners")
        }
    }

    private fun loadAllBannersFromAssets() {
        val assetsDir = File("assets/banners")

        if (!assetsDir.exists() || !assetsDir.isDirectory) {
            println("❌ Assets directory not found: assets/banners")
            return
        }

        val imageFiles = assetsDir.listFiles { file ->
            file.isFile && file.name.lowercase().matches(Regex(".*\\.(png|jpg|jpeg|gif|webp)"))
        }?.sortedBy { it.name } ?: emptyList()

        println("📸 Found ${imageFiles.size} banner images")

        val banners = imageFiles.mapIndexed { index, file ->
            createBannerFromImage(file, index + 1)
        }

        bannerRepository.saveAll(banners)

        println("✅ Loaded ${banners.size} banners successfully")
    }

    private fun createBannerFromImage(imageFile: File, priority: Int): Banner {
        val fileName = imageFile.nameWithoutExtension
        val imageUrl = "/assets/banners/${imageFile.name}"

        // Check if this is a diet food banner
        val isDietBanner = fileName.contains("diet") ||
                fileName.contains("healthy") ||
                fileName.contains("protein") ||
                fileName.contains("salad") ||
                fileName.contains("bowl")

        val bannerType = when {
            isDietBanner -> BannerType.DIET_FOOD
            priority in 1..3 -> BannerType.HOME_PAGE
            priority in 4..6 -> BannerType.PROMOTIONAL
            priority in 7..9 -> BannerType.FLASH_SALE
            else -> BannerType.CATEGORY_PAGE
        }

        val category = when {
            isDietBanner -> "DIET_FOOD_CAT"
            priority in 1..3 -> "BURGERS_CAT"
            priority in 4..6 -> "PIZZA_CAT"
            priority in 7..9 -> "SUSHI_CAT"
            else -> "BEVERAGES_CAT"
        }

        val superCategory = if (isDietBanner) "FOOD_SUPER" else getSuperCategoryId(priority)

        val title = fileName.replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

        return Banner(
            bannerId = generateBannerId(),
            title = title,
            description = "Try $title now",
            shortDescription = "Best $title",
            superCategory = superCategory,
            category = category,
            subCategory = if (isDietBanner) "HIGH_PROTEIN" else getSubCategoryId(priority),
            imageUrl = imageUrl,
            thumbnailUrl = imageUrl,
            mobileImageUrl = imageUrl,
            tabletImageUrl = imageUrl,
            imageMetadata = ImageMetadata(
                originalFileName = imageFile.name,
                fileSize = imageFile.length(),
                mimeType = "image/png",
                width = 1920,
                height = 1080,
                aspectRatio = 1.78,
                colors = emptyList(),
                dominantColor = "#FF5733",
                altText = title,
                title = title,
                compressionRatio = 0.85,
                hash = UUID.randomUUID().toString()
            ),
            drawableResourceId = null,
            resourceName = fileName,
            resourcePath = imageUrl,
            bannerType = bannerType,
            priority = priority,
            isActive = true,
            status = ImageStatus.ACTIVE,
            targetRoles = null,
            targetLocations = null,
            targetDevices = null,
            startDate = null,
            endDate = null,
            clickUrl = "/menu",
            deepLink = "app://menu",
            actionType = null,
            actionData = null,
            clickCount = 0,
            viewCount = 0,
            ctr = 0.0,
            tags = listOf("auto", "banner"),
            metadata = mapOf(
                "source" to "batch_loader",
                "file" to imageFile.name
            ),
            // Add diet fields for diet banners
            price = if (isDietBanner) getDefaultPrice(title) else null,
            restaurantName = if (isDietBanner) getDefaultRestaurant(title) else null,
            rating = if (isDietBanner) "4.5" else null,
            deliveryTime = if (isDietBanner) "20-25 mins" else null,
            distance = if (isDietBanner) "2.0 km" else null,
            discount = if (isDietBanner) "10%" else null,
            discountAmount = if (isDietBanner) "₹20" else null,
            address = if (isDietBanner) "Delivery available" else null,
            calories = if (isDietBanner) getDefaultCalories(title) else null,
            protein = if (isDietBanner) getDefaultProtein(title) else null,
            isHighProtein = isDietBanner,
            version = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            createdBy = "system",
            updatedBy = "system"
        )
    }

    // =========================
    // STRING ID RESOLVERS
    // =========================

    private fun getSuperCategoryId(priority: Int): String {
        return when (priority) {
            in 1..5 -> "FOOD_SUPER"
            in 6..10 -> "BEVERAGES_SUPER"
            else -> "DESSERTS_SUPER"
        }
    }

    private fun getCategoryId(priority: Int): String {
        return when (priority) {
            1, 2, 3 -> "BURGERS_CAT"
            4, 5, 6 -> "PIZZA_CAT"
            7, 8, 9 -> "SUSHI_CAT"
            else -> "BEVERAGES_CAT"
        }
    }

    private fun getSubCategoryId(priority: Int): String? {
        return when (priority) {
            1 -> "CHICKEN_BURGER"
            4 -> "MARGHERITA"
            7 -> "CALIFORNIA_ROLL"
            else -> null
        }
    }

    // =========================
    // DEFAULT VALUES FOR DIET BANNERS
    // =========================

    private fun getDefaultPrice(title: String): String {
        return when {
            title.contains("Chicken", ignoreCase = true) -> "180"
            title.contains("Paneer", ignoreCase = true) -> "160"
            title.contains("Oats", ignoreCase = true) -> "110"
            title.contains("Salad", ignoreCase = true) -> "150"
            title.contains("Bowl", ignoreCase = true) -> "140"
            else -> "99"
        }
    }

    private fun getDefaultRestaurant(title: String): String {
        return when {
            title.contains("Chicken", ignoreCase = true) -> "Fit Feast"
            title.contains("Paneer", ignoreCase = true) -> "Healthy Mash"
            title.contains("Oats", ignoreCase = true) -> "Muscle Bowl"
            title.contains("Salad", ignoreCase = true) -> "Green Plate"
            title.contains("Bowl", ignoreCase = true) -> "Power Bowl"
            else -> "Healthy Kitchen"
        }
    }

    private fun getDefaultCalories(title: String): String {
        return when {
            title.contains("Chicken", ignoreCase = true) -> "320"
            title.contains("Paneer", ignoreCase = true) -> "350"
            title.contains("Oats", ignoreCase = true) -> "390"
            title.contains("Salad", ignoreCase = true) -> "280"
            title.contains("Bowl", ignoreCase = true) -> "300"
            else -> "250"
        }
    }

    private fun getDefaultProtein(title: String): String {
        return when {
            title.contains("Chicken", ignoreCase = true) -> "28"
            title.contains("Paneer", ignoreCase = true) -> "22"
            title.contains("Oats", ignoreCase = true) -> "17"
            title.contains("Salad", ignoreCase = true) -> "20"
            title.contains("Bowl", ignoreCase = true) -> "18"
            else -> "15"
        }
    }

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
    }
}