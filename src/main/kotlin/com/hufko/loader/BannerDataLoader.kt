package com.hufko.loader

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hufko.dto.BannerJsonData
import com.hufko.enums.ImageStatus
import com.hufko.model.*
import com.hufko.repository.BannerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class BannerDataLoader(
    private val bannerRepository: BannerRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (bannerRepository.count() == 0L) {
            println("📦 Loading banners from JSON file...")
            loadBannersFromJson()
        } else {
            println("✅ Database already contains ${bannerRepository.count()} banners")
        }
    }

    private fun loadBannersFromJson() {
        val objectMapper = jacksonObjectMapper()

        try {
            val inputStream = javaClass.getResourceAsStream("/banner_data.json")
            if (inputStream == null) {
                println("❌ banner_data.json not found in resources")
                return
            }

            val bannerDataList: List<BannerJsonData> = objectMapper.readValue(inputStream)

            // Pre-define super categories
            val superCategories = mapOf(
                "FOOD_SUPER" to SuperCategory(
                    id = "FOOD_SUPER",
                    name = "Food",
                    code = "FOOD",
                    description = "All food categories",
                    iconUrl = "/assets/icons/food.png"
                )
            )

            // Pre-define categories
            val categories = mapOf(
                "ALL_FOOD_CAT" to Category(
                    id = "ALL_FOOD_CAT",
                    name = "All Food",
                    code = "ALL_FOOD",
                    description = "Complete food collection",
                    iconUrl = "/assets/icons/all_food.png",
                    displayOrder = 1
                ),
                "HEALTHY_FOOD_CAT" to Category(
                    id = "HEALTHY_FOOD_CAT",
                    name = "Healthy Food",
                    code = "HEALTHY_FOOD",
                    description = "Organic and healthy options",
                    iconUrl = "/assets/icons/healthy.png",
                    displayOrder = 2
                )
            )

            val banners = bannerDataList.map { data ->
                val superCategory = superCategories[data.superCategory]
                    ?: throw RuntimeException("Super category not found: ${data.superCategory}")

                val category = categories[data.category]
                    ?: throw RuntimeException("Category not found: ${data.category}")

                Banner(
                    bannerId = generateBannerId(),
                    title = data.title,
                    description = data.description,
                    shortDescription = data.shortDescription,
                    superCategory = superCategory,
                    category = category,
                    subCategory = null,
                    imageUrl = data.imageUrl,
                    thumbnailUrl = data.thumbnailUrl,
                    mobileImageUrl = data.mobileImageUrl,
                    tabletImageUrl = data.tabletImageUrl,
                    imageMetadata = ImageMetadata(
                        originalFileName = data.imageUrl.substringAfterLast("/"),
                        fileSize = 0,
                        mimeType = "image/png",
                        width = 1920,
                        height = 1080,
                        aspectRatio = 1.78,
                        colors = emptyList(),
                        dominantColor = "#FF5733",
                        altText = data.title,
                        title = data.title,
                        compressionRatio = 0.85,
                        hash = UUID.randomUUID().toString()
                    ),
                    drawableResourceId = null,
                    resourceName = data.resourceName,
                    resourcePath = data.imageUrl,
                    bannerType = data.bannerType,
                    priority = data.priority,
                    isActive = true,
                    status = ImageStatus.ACTIVE,
                    targetRoles = null,
                    targetLocations = null,
                    targetDevices = null,
                    startDate = null,
                    endDate = null,
                    clickUrl = data.clickUrl,
                    deepLink = data.deepLink,
                    actionType = null,
                    actionData = null,
                    clickCount = 0,
                    viewCount = 0,
                    ctr = 0.0,
                    tags = data.tags,
                    metadata = mapOf("source" to "json_loader"),
                    version = 0,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    createdBy = "system",
                    updatedBy = "system"
                )
            }

            bannerRepository.saveAll(banners)
            println("✅ Successfully loaded ${banners.size} banners from JSON")

            // Print summary
            banners.forEach { banner ->
                println("   - ${banner.title} (${banner.bannerType}) - Priority: ${banner.priority}")
            }

        } catch (e: Exception) {
            println("❌ Error loading banner data: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().substring(0, 8)}"
    }
}