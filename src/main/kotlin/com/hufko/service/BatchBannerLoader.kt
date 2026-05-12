package com.hufko.service

import com.hufko.enums.BannerType
import com.hufko.enums.ImageStatus
import com.hufko.model.*
import com.hufko.repository.BannerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime
import java.util.*

@Component
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

        val bannerType = when (priority) {
            in 1..3 -> BannerType.HOME_PAGE
            in 4..6 -> BannerType.PROMOTIONAL
            in 7..9 -> BannerType.FLASH_SALE
            else -> BannerType.CATEGORY_PAGE
        }

        val title = fileName.replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

        return Banner(
            bannerId = generateBannerId(),
            title = title,
            description = "Try $title now",
            shortDescription = "Best $title",

            // ✅ FIXED: STRING IDS ONLY (NO OBJECTS)
            superCategory = getSuperCategoryId(priority),
            category = getCategoryId(priority),
            subCategory = getSubCategoryId(priority),

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

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
    }
}