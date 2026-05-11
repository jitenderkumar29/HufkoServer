//package com.hufko
//
//import com.hufko.enums.BannerType
//import com.hufko.enums.ImageStatus
//import com.hufko.model.*
//import com.hufko.repository.BannerRepository
//import org.springframework.boot.CommandLineRunner
//import org.springframework.data.mongodb.core.MongoTemplate
//import org.springframework.stereotype.Component
//import java.time.LocalDateTime
//import java.util.*
//
//@Component
//class DatabaseInitializer(
//    private val bannerRepository: BannerRepository,
//    private val mongoTemplate: MongoTemplate
//) : CommandLineRunner {
//
//    override fun run(vararg args: String) {
//        val dbName = mongoTemplate.db.name
//        println("\n📊 Working with database: $dbName")
//
//        if (bannerRepository.count() == 0L) {
//            println("🎨 Initializing banner data in $dbName...")
//            createSampleBanners()
//        } else {
//            println("✅ Database already contains ${bannerRepository.count()} banners")
//        }
//    }
//
//    private fun createSampleBanners() {
//        val banners = listOf(
//            createBanner(
//                title = "Summer Sale",
//                description = "Get up to 50% off on summer collection",
//                resourceName = "summer_sale",
//                priority = 1,
//                bannerType = BannerType.HOME_PAGE,
//                clickUrl = "/sale/summer",
//                deepLink = "app://summer-sale"
//            ),
//            createBanner(
//                title = "New Arrivals",
//                description = "Check out our latest collection",
//                resourceName = "new_arrivals",
//                priority = 2,
//                bannerType = BannerType.PROMOTIONAL,
//                clickUrl = "/products/new",
//                deepLink = "app://new-arrivals"
//            ),
//            createBanner(
//                title = "Flash Sale",
//                description = "Limited time offer",
//                resourceName = "flash_sale",
//                priority = 1,
//                bannerType = BannerType.FLASH_SALE,
//                clickUrl = "/sale/flash",
//                deepLink = "app://flash-sale"
//            )
//        )
//
//        bannerRepository.saveAll(banners)
//        println("✅ Created ${banners.size} sample banners in database")
//    }
//
//    private fun createBanner(
//        title: String,
//        description: String,
//        resourceName: String,
//        priority: Int,
//        bannerType: BannerType,
//        clickUrl: String,
//        deepLink: String
//    ): Banner {
//        return Banner(
//            bannerId = generateBannerId(),
//            title = title,
//            description = description,
//            shortDescription = description.take(50),
//            superCategory = SuperCategory(
//                id = "SUPER_001",
//                name = "General",
//                code = "GEN",
//                description = "General category"
//            ),
//            category = Category(
//                id = "CAT_001",
//                name = "All Products",
//                code = "ALL",
//                description = "All products",
//                displayOrder = 1
//            ),
//            subCategory = null,
//            imageUrl = "/images/banners/$resourceName.jpg",
//            thumbnailUrl = "/images/banners/thumb_$resourceName.jpg",
//            mobileImageUrl = null,
//            tabletImageUrl = null,
//            imageMetadata = ImageMetadata(
//                originalFileName = "$resourceName.jpg",
//                fileSize = 102400,
//                mimeType = "image/jpeg",
//                width = 1920,
//                height = 1080,
//                aspectRatio = 1.78,
//                colors = emptyList(),
//                dominantColor = "#FF5733",
//                altText = title,
//                title = title,
//                compressionRatio = 0.85,
//                hash = UUID.randomUUID().toString()
//            ),
//            drawableResourceId = null,
//            resourceName = resourceName,
//            resourcePath = null,
//            bannerType = bannerType,
//            priority = priority,
//            isActive = true,
//            status = ImageStatus.ACTIVE,
//            targetRoles = null,
//            targetLocations = null,
//            targetDevices = null,
//            startDate = null,
//            endDate = null,
//            clickUrl = clickUrl,
//            deepLink = deepLink,
//            actionType = null,
//            actionData = null,
//            clickCount = 0,
//            viewCount = 0,
//            ctr = 0.0,
//            tags = listOf(title.lowercase(), "featured"),
//            metadata = emptyMap(),
//            version = 0,
//            createdAt = LocalDateTime.now(),
//            updatedAt = LocalDateTime.now(),
//            createdBy = "system",
//            updatedBy = "system"
//        )
//    }
//
//    private fun generateBannerId(): String {
//        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().substring(0, 8)}"
//    }
//}