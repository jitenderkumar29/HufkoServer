package com.hufko.loader

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hufko.dto.BannerJsonData
import com.hufko.enums.ImageStatus
import com.hufko.model.Banner
import com.hufko.model.ImageMetadata
import com.hufko.repository.BannerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class BannerDataLoader(
    private val bannerRepository: BannerRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (bannerRepository.count() == 0L) {
            println("📦 Loading banners from JSON file...")
            loadBannersFromJson()
        } else {
            println("✅ Database already contains ${bannerRepository.count()} banners in BannerDataLoader file")
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

            val bannerDataList: List<BannerJsonData> =
                objectMapper.readValue(inputStream)

            println("📄 Found ${bannerDataList.size} banners in JSON")

            val banners = bannerDataList.map { data ->

                Banner(
                    bannerId = generateBannerId(),

                    title = data.title,
                    description = data.description,
                    shortDescription = data.shortDescription,

                    // DIRECT JSON VALUES
                    superCategory = data.superCategory,
                    category = data.category,
                    subCategory = data.subCategory,

                    imageUrl = data.imageUrl,
                    thumbnailUrl = data.thumbnailUrl,
                    mobileImageUrl = data.mobileImageUrl,
                    tabletImageUrl = data.tabletImageUrl,

                    imageMetadata = ImageMetadata(
                        originalFileName = "${data.resourceName}.png",
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

                    tags = data.tags ?: emptyList(),
                    metadata = mapOf(
                        "source" to "json_loader"
                    ),

                    version = 0,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    createdBy = "system",
                    updatedBy = "system"
                )
            }

            bannerRepository.saveAll(banners)

            println("✅ Successfully loaded ${banners.size} banners from JSON")

            banners.forEach { banner ->
                println(
                    "• ${banner.title} (${banner.bannerType}) - " +
                            "${banner.superCategory}/${banner.category}/${banner.subCategory}"
                )
            }

        } catch (e: Exception) {
            println("❌ Error loading banner data: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${
            UUID.randomUUID().toString().substring(0, 8)
        }"
    }
}