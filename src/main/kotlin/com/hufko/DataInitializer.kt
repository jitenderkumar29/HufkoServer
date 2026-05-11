package com.hufko

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hufko.dto.BannerDataDTO
import com.hufko.enums.BannerType
import com.hufko.enums.ImageStatus
import com.hufko.model.*
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

            // Pre-define super categories mapping
            val superCategories = mapOf(
                "FOOD_SUPER" to SuperCategory(
                    id = "FOOD_SUPER",
                    name = "Food",
                    code = "FOOD",
                    description = "All food categories",
                    iconUrl = "/assets/icons/food.png"
                ),
                "BEVERAGES_SUPER" to SuperCategory(
                    id = "BEVERAGES_SUPER",
                    name = "Beverages",
                    code = "BEVERAGES",
                    description = "Drinks and beverages",
                    iconUrl = "/assets/icons/beverages.png"
                ),
                "DESSERTS_SUPER" to SuperCategory(
                    id = "DESSERTS_SUPER",
                    name = "Desserts",
                    code = "DESSERTS",
                    description = "Sweet treats",
                    iconUrl = "/assets/icons/desserts.png"
                )
            )

            // Pre-define categories mapping
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
                ),
                "BURGERS_CAT" to Category(
                    id = "BURGERS_CAT",
                    name = "Burgers",
                    code = "BURGERS",
                    description = "Delicious burgers",
                    iconUrl = "/assets/icons/burgers.png",
                    displayOrder = 3
                ),
                "PIZZA_CAT" to Category(
                    id = "PIZZA_CAT",
                    name = "Pizza",
                    code = "PIZZA",
                    description = "Authentic pizzas",
                    iconUrl = "/assets/icons/pizza.png",
                    displayOrder = 4
                ),
                "SUSHI_CAT" to Category(
                    id = "SUSHI_CAT",
                    name = "Sushi",
                    code = "SUSHI",
                    description = "Fresh sushi",
                    iconUrl = "/assets/icons/sushi.png",
                    displayOrder = 5
                ),
                "BEVERAGES_CAT" to Category(
                    id = "BEVERAGES_CAT",
                    name = "Beverages",
                    code = "BEVERAGES",
                    description = "Refreshing drinks",
                    iconUrl = "/assets/icons/beverages.png",
                    displayOrder = 6
                ),
                "DESSERTS_CAT" to Category(
                    id = "DESSERTS_CAT",
                    name = "Desserts",
                    code = "DESSERTS",
                    description = "Sweet desserts",
                    iconUrl = "/assets/icons/desserts.png",
                    displayOrder = 7
                ),
                "SEAFOOD_CAT" to Category(
                    id = "SEAFOOD_CAT",
                    name = "Seafood",
                    code = "SEAFOOD",
                    description = "Fresh seafood",
                    iconUrl = "/assets/icons/seafood.png",
                    displayOrder = 8
                ),
                "BREAKFAST_CAT" to Category(
                    id = "BREAKFAST_CAT",
                    name = "Breakfast",
                    code = "BREAKFAST",
                    description = "Morning delights",
                    iconUrl = "/assets/icons/breakfast.png",
                    displayOrder = 9
                ),
                "VEGAN_CAT" to Category(
                    id = "VEGAN_CAT",
                    name = "Vegan",
                    code = "VEGAN",
                    description = "Plant-based options",
                    iconUrl = "/assets/icons/vegan.png",
                    displayOrder = 10
                ),
                "MEXICAN_CAT" to Category(
                    id = "MEXICAN_CAT",
                    name = "Mexican",
                    code = "MEXICAN",
                    description = "Mexican cuisine",
                    iconUrl = "/assets/icons/mexican.png",
                    displayOrder = 11
                ),
                "CHINESE_CAT" to Category(
                    id = "CHINESE_CAT",
                    name = "Chinese",
                    code = "CHINESE",
                    description = "Chinese cuisine",
                    iconUrl = "/assets/icons/chinese.png",
                    displayOrder = 12
                ),
                "INDIAN_CAT" to Category(
                    id = "INDIAN_CAT",
                    name = "Indian",
                    code = "INDIAN",
                    description = "Indian cuisine",
                    iconUrl = "/assets/icons/indian.png",
                    displayOrder = 13
                ),
                "MEDITERRANEAN_CAT" to Category(
                    id = "MEDITERRANEAN_CAT",
                    name = "Mediterranean",
                    code = "MEDITERRANEAN",
                    description = "Mediterranean cuisine",
                    iconUrl = "/assets/icons/mediterranean.png",
                    displayOrder = 14
                )
            )

            val banners = bannerDataList.map { data ->
                val superCategory = superCategories[data.superCategory]
                    ?: throw RuntimeException("Super category not found: ${data.superCategory}")

                val category = categories[data.category]
                    ?: throw RuntimeException("Category not found: ${data.category}")

                createBannerFromData(data, superCategory, category)
            }

            bannerRepository.saveAll(banners)
            println("✅ Successfully loaded ${banners.size} banners from JSON file into database")

            // Print summary
            println("\n📊 Banner Summary:")
            banners.forEach { banner ->
                println("   ${banner.priority}. ${banner.title} (${banner.bannerType}) - ${banner.category.name}")
            }

        } catch (e: Exception) {
            println("❌ Error loading banner data from JSON: ${e.message}")
            e.printStackTrace()
            println("⚠️ Falling back to creating sample banners...")
            createSampleBanners()
        }
    }

    private fun createBannerFromData(
        data: BannerDataDTO,
        superCategory: SuperCategory,
        category: Category
    ): Banner {
        // Determine subCategory if provided
        val subCategory = data.subCategory?.let { subId ->
            SubCategory(
                id = subId,
                name = subId.replace("_", " ").split(" ").joinToString(" ") {
                    it.replaceFirstChar { char -> char.uppercase() }
                },
                code = subId,
                description = "$subId subcategory",
                parentCategoryId = category.id
            )
        }

        return Banner(
            bannerId = generateBannerId(),
            title = data.title,
            description = data.description,
            shortDescription = data.shortDescription ?: data.description.take(50),
            superCategory = superCategory,
            category = category,
            subCategory = subCategory,
            imageUrl = data.imageUrl,
            thumbnailUrl = data.thumbnailUrl,
            mobileImageUrl = data.mobileImageUrl ?: data.imageUrl,
            tabletImageUrl = data.tabletImageUrl ?: data.imageUrl,
            imageMetadata = ImageMetadata(
                originalFileName = data.imageUrl.substringAfterLast("/"),
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
            metadata = mapOf(
                "source" to "json_file",
                "version" to "1.0",
                "assetPath" to data.imageUrl
            ),
            version = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            createdBy = "system",
            updatedBy = "system"
        )
    }

    private fun createSampleBanners() {
        try {
            val superCategory = SuperCategory(
                id = "FOOD_SUPER",
                name = "Food",
                code = "FOOD",
                description = "All food categories"
            )

            val category = Category(
                id = "SAMPLE_CAT",
                name = "Sample Category",
                code = "SAMPLE",
                description = "Sample category",
                iconUrl = null,
                displayOrder = 1
            )

            val banners = (1..5).map { index ->
                createSampleBanner(
                    title = "Sample Banner $index",
                    description = "This is sample banner $index",
                    shortDescription = "Sample $index",
                    resourceName = "sample_banner_$index",
                    priority = index,
                    bannerType = if (index == 1) BannerType.HOME_PAGE else BannerType.PROMOTIONAL,
                    category = category,
                    clickUrl = "/sample/$index",
                    deepLink = "app://sample/$index",
                    tags = listOf("sample", "banner", "demo"),
                    superCategory = superCategory
                )
            }

            bannerRepository.saveAll(banners)
            println("✅ Successfully created ${banners.size} sample banners")

        } catch (e: Exception) {
            println("❌ Error creating sample banners: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun createSampleBanner(
        title: String,
        description: String,
        shortDescription: String,
        resourceName: String,
        priority: Int,
        bannerType: BannerType,
        category: Category,
        clickUrl: String,
        deepLink: String,
        tags: List<String>,
        superCategory: SuperCategory
    ): Banner {
        return Banner(
            bannerId = generateBannerId(),
            title = title,
            description = description,
            shortDescription = shortDescription,
            superCategory = superCategory,
            category = category,
            subCategory = null,
            imageUrl = "/assets/banners/$resourceName.png",
            thumbnailUrl = "/assets/banners/$resourceName.png",
            mobileImageUrl = "/assets/banners/$resourceName.png",
            tabletImageUrl = "/assets/banners/$resourceName.png",
            imageMetadata = ImageMetadata(
                originalFileName = "$resourceName.png",
                fileSize = 1024 * 100,
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
            resourceName = resourceName,
            resourcePath = null,
            bannerType = bannerType,
            priority = priority,
            isActive = true,
            status = ImageStatus.ACTIVE,
            targetRoles = null,
            targetLocations = null,
            targetDevices = null,
            startDate = null,
            endDate = null,
            clickUrl = clickUrl,
            deepLink = deepLink,
            actionType = null,
            actionData = null,
            clickCount = 0,
            viewCount = 0,
            ctr = 0.0,
            tags = tags,
            metadata = mapOf("source" to "fallback"),
            version = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            createdBy = "system",
            updatedBy = "system"
        )
    }

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().substring(0, 8)}"
    }
}