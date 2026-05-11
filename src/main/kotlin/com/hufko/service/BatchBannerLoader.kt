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
        val assetsPath = "assets/banners"
        val assetsDir = File(assetsPath)

        if (!assetsDir.exists() || !assetsDir.isDirectory) {
            println("❌ Assets directory not found: $assetsPath")
            println("📁 Please create folder: $assetsPath and add banner images")
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
        println("✅ Successfully loaded ${banners.size} banners into database")

        // Print summary
        println("\n📊 Banner Summary:")
        banners.forEach { banner ->
            println("   ${banner.priority}. ${banner.title} - ${banner.bannerType}")
        }
    }

    private fun createBannerFromImage(imageFile: File, priority: Int): Banner {
        val fileName = imageFile.nameWithoutExtension
        val imageUrl = "/assets/banners/${imageFile.name}"

        // Determine banner type based on priority
        val bannerType = when (priority) {
            in 1..3 -> BannerType.HOME_PAGE
            in 4..6 -> BannerType.PROMOTIONAL
            in 7..9 -> BannerType.FLASH_SALE
            in 10..12 -> BannerType.CATEGORY_PAGE
            else -> BannerType.PROMOTIONAL
        }

        // Create title from filename
        val title = fileName
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }

        return Banner(
            bannerId = generateBannerId(),
            title = title,
            description = getDescriptionForBanner(priority, title),
            shortDescription = getShortDescriptionForBanner(priority),
            superCategory = getSuperCategory(priority),
            category = getCategory(priority),
            subCategory = getSubCategory(priority),
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
            clickUrl = getClickUrl(priority),
            deepLink = getDeepLink(priority),
            actionType = null,
            actionData = null,
            clickCount = 0,
            viewCount = 0,
            ctr = 0.0,
            tags = getTagsForBanner(priority),
            metadata = mapOf(
                "source" to "batch_loader",
                "imageFile" to imageFile.name,
                "loadedAt" to LocalDateTime.now().toString()
            ),
            version = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            createdBy = "system",
            updatedBy = "system"
        )
    }

    private fun getSuperCategory(priority: Int): SuperCategory {
        return when (priority) {
            in 1..5 -> SuperCategory(
                id = "FOOD_SUPER",
                name = "Food",
                code = "FOOD",
                description = "All food categories",
                iconUrl = "/assets/icons/food.png"
            )
            in 6..10 -> SuperCategory(
                id = "BEVERAGES_SUPER",
                name = "Beverages",
                code = "BEVERAGES",
                description = "Drinks and beverages",
                iconUrl = "/assets/icons/beverages.png"
            )
            else -> SuperCategory(
                id = "DESSERTS_SUPER",
                name = "Desserts",
                code = "DESSERTS",
                description = "Sweet treats",
                iconUrl = "/assets/icons/desserts.png"
            )
        }
    }

    private fun getCategory(priority: Int): Category {
        return when (priority) {
            1, 2, 3 -> Category(
                id = "BURGERS_CAT",
                name = "Burgers",
                code = "BURGERS",
                description = "Delicious burgers",
                iconUrl = "/assets/icons/burgers.png",
                displayOrder = 1
            )
            4, 5, 6 -> Category(
                id = "PIZZA_CAT",
                name = "Pizza",
                code = "PIZZA",
                description = "Authentic pizzas",
                iconUrl = "/assets/icons/pizza.png",
                displayOrder = 2
            )
            7, 8, 9 -> Category(
                id = "SUSHI_CAT",
                name = "Sushi",
                code = "SUSHI",
                description = "Fresh sushi",
                iconUrl = "/assets/icons/sushi.png",
                displayOrder = 3
            )
            10, 11, 12 -> Category(
                id = "BEVERAGES_CAT",
                name = "Beverages",
                code = "BEVERAGES",
                description = "Refreshing drinks",
                iconUrl = "/assets/icons/beverages.png",
                displayOrder = 4
            )
            else -> Category(
                id = "DESSERTS_CAT",
                name = "Desserts",
                code = "DESSERTS",
                description = "Sweet desserts",
                iconUrl = "/assets/icons/desserts.png",
                displayOrder = 5
            )
        }
    }

    private fun getSubCategory(priority: Int): SubCategory? {
        return when (priority) {
            1 -> SubCategory(
                id = "CHICKEN_BURGER",
                name = "Chicken Burgers",
                code = "CHICKEN_BURGER",
                description = "Delicious chicken burgers",
                parentCategoryId = "BURGERS_CAT"
            )
            4 -> SubCategory(
                id = "MARGHERITA",
                name = "Margherita Pizza",
                code = "MARGHERITA",
                description = "Classic margherita",
                parentCategoryId = "PIZZA_CAT"
            )
            7 -> SubCategory(
                id = "CALIFORNIA_ROLL",
                name = "California Roll",
                code = "CALIFORNIA_ROLL",
                description = "Fresh California rolls",
                parentCategoryId = "SUSHI_CAT"
            )
            else -> null
        }
    }

    private fun getDescriptionForBanner(priority: Int, title: String): String {
        return when (priority) {
            in 1..3 -> "Delicious $title made with fresh ingredients. Order now and get special discounts!"
            in 4..6 -> "Authentic $title with premium toppings. Best pizza in town!"
            in 7..9 -> "Fresh and authentic $title prepared by expert chefs. Taste of Japan!"
            in 10..12 -> "Refreshing $title to quench your thirst. Buy one get one free!"
            else -> "Amazing $title at unbeatable prices. Limited time offer!"
        }
    }

    private fun getShortDescriptionForBanner(priority: Int): String {
        return when (priority) {
            in 1..3 -> "Best burgers in town"
            in 4..6 -> "Authentic pizzas"
            in 7..9 -> "Fresh sushi"
            in 10..12 -> "Refreshing drinks"
            else -> "Sweet treats"
        }
    }

    private fun getClickUrl(priority: Int): String {
        return when (priority) {
            in 1..3 -> "/menu/burgers"
            in 4..6 -> "/menu/pizza"
            in 7..9 -> "/menu/sushi"
            in 10..12 -> "/menu/beverages"
            else -> "/menu/desserts"
        }
    }

    private fun getDeepLink(priority: Int): String {
        return when (priority) {
            in 1..3 -> "app://menu/burgers"
            in 4..6 -> "app://menu/pizza"
            in 7..9 -> "app://menu/sushi"
            in 10..12 -> "app://menu/beverages"
            else -> "app://menu/desserts"
        }
    }

    private fun getTagsForBanner(priority: Int): List<String> {
        val baseTags = mutableListOf("featured", "popular")

        return when (priority) {
            in 1..3 -> baseTags + listOf("burgers", "fastfood", "nonveg")
            in 4..6 -> baseTags + listOf("pizza", "italian", "cheese")
            in 7..9 -> baseTags + listOf("sushi", "japanese", "fresh")
            in 10..12 -> baseTags + listOf("beverages", "drinks", "refreshing")
            else -> baseTags + listOf("desserts", "sweet", "treats")
        }
    }

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().substring(0, 8)}"
    }
}