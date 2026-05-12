package com.hufko

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hufko.dto.BannerDataDTO
import com.hufko.enums.BannerType
import com.hufko.enums.ImageStatus
import com.hufko.model.Banner
import com.hufko.model.ImageMetadata
import com.hufko.repository.BannerRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class DataInitializer(
    private val bannerRepository: BannerRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (bannerRepository.count() == 0L) {
            println("📦 Initializing database with banner data...")
            loadBannersFromJsonFile()
        } else {
            println("✅ Database already contains ${bannerRepository.count()} banners")
        }
    }

    private fun loadBannersFromJsonFile() {
        try {
            val objectMapper = jacksonObjectMapper()

            // Read JSON file from resources
            val resource = ClassPathResource("banner_data.json")
            val inputStream = resource.inputStream

            val bannerDataList: List<BannerDataDTO> = objectMapper.readValue(inputStream)

            println("📄 Found ${bannerDataList.size} banners in JSON file")

            val banners = bannerDataList.map { data ->
                createBannerFromData(data)
            }

            bannerRepository.saveAll(banners)
            println("✅ Successfully loaded ${banners.size} banners from JSON file into database")

            // Print summary
            println("\n📊 Banner Summary:")
            banners.forEach { banner ->
                println("   ${banner.priority}. ${banner.title} (${banner.bannerType}) - ${banner.category}")
            }

        } catch (e: Exception) {
            println("❌ Error loading banner data from JSON: ${e.message}")
            e.printStackTrace()
            println("⚠️ Falling back to creating sample banners...")
            createSampleBanners()
        }
    }

    private fun createBannerFromData(data: BannerDataDTO): Banner {
        return Banner(
            bannerId = generateBannerId(),

            title = data.title,
            description = data.description,
            shortDescription = data.shortDescription,

            // Use direct string values from JSON
            superCategory = data.superCategory,
            category = data.category,
            subCategory = data.subCategory,

            imageUrl = data.imageUrl,
            thumbnailUrl = data.thumbnailUrl,
            mobileImageUrl = data.mobileImageUrl ?: data.imageUrl,
            tabletImageUrl = data.tabletImageUrl ?: data.imageUrl,

            imageMetadata = ImageMetadata(
                originalFileName = "${data.resourceName}.png",
                fileSize = 1024 * 100,
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
            metadata = mapOf("source" to "json_file"),

            version = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            createdBy = "system",
            updatedBy = "system"
        )
    }

    private fun createSampleBanners() {
        val banners = (1..5).map { index ->
            Banner(
                bannerId = generateBannerId(),
                title = "Sample Banner $index",
                description = "This is sample banner $index",
                shortDescription = "Sample $index",
                superCategory = "FOOD_SUPER",
                category = "SAMPLE_CAT",
                subCategory = null,
                imageUrl = "/assets/banners/sample_banner_$index.png",
                thumbnailUrl = "/assets/banners/sample_banner_$index.png",
                mobileImageUrl = "/assets/banners/sample_banner_$index.png",
                tabletImageUrl = "/assets/banners/sample_banner_$index.png",
                imageMetadata = ImageMetadata(
                    originalFileName = "sample_banner_$index.png",
                    fileSize = 1024 * 100,
                    mimeType = "image/png",
                    width = 1920,
                    height = 1080,
                    aspectRatio = 1.78,
                    colors = emptyList(),
                    dominantColor = "#FF5733",
                    altText = "Sample Banner $index",
                    title = "Sample Banner $index",
                    compressionRatio = 0.85,
                    hash = UUID.randomUUID().toString()
                ),
                drawableResourceId = null,
                resourceName = "sample_banner_$index",
                resourcePath = null,
                bannerType = if (index == 1) BannerType.HOME_PAGE else BannerType.PROMOTIONAL,
                priority = index,
                isActive = true,
                status = ImageStatus.ACTIVE,
                targetRoles = null,
                targetLocations = null,
                targetDevices = null,
                startDate = null,
                endDate = null,
                clickUrl = "/sample/$index",
                deepLink = "app://sample/$index",
                actionType = null,
                actionData = null,
                clickCount = 0,
                viewCount = 0,
                ctr = 0.0,
                tags = listOf("sample", "banner", "demo"),
                metadata = mapOf("source" to "fallback"),
                version = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                createdBy = "system",
                updatedBy = "system"
            )
        }

        bannerRepository.saveAll(banners)
        println("✅ Successfully created ${banners.size} sample banners")
    }

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().substring(0, 8)}"
    }
}