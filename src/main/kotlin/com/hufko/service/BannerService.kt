package com.hufko.service

import com.hufko.dto.*
import com.hufko.enums.BannerType
import com.hufko.enums.ImageStatus
import com.hufko.model.*
import com.hufko.repository.BannerRepository
import com.hufko.repository.CategoryRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
class BannerService(
    private val bannerRepository: BannerRepository,
    private val storageService: StorageService,
    private val categoryRepository: CategoryRepository
) {

    @CacheEvict(value = ["banners"], allEntries = true)
    fun createBanner(file: MultipartFile, bannerData: BannerCreateDTO): BannerDTO {
        // Get category hierarchy
        val superCategory = getSuperCategory(bannerData.superCategoryId)
        val category = getCategory(bannerData.categoryId)
        val subCategory = bannerData.subCategoryId?.let { getSubCategory(it) }

        // Upload image and generate URLs
        val imageUrls = storageService.storeBannerImage(file, bannerData.categoryId)
        val metadata = storageService.extractImageMetadata(file)

        val banner = Banner(
            bannerId = generateBannerId(),
            title = bannerData.title,
            description = bannerData.description,
            shortDescription = bannerData.shortDescription,
            superCategory = superCategory,
            category = category,
            subCategory = subCategory,
            imageUrl = imageUrls.originalUrl,
            thumbnailUrl = imageUrls.thumbnailUrl,
            mobileImageUrl = imageUrls.mobileUrl,
            tabletImageUrl = imageUrls.tabletUrl,
            imageMetadata = metadata,
            resourceName = bannerData.resourceName,
            resourcePath = imageUrls.originalUrl,
            bannerType = bannerData.bannerType,
            priority = bannerData.priority,
            isActive = true,
            status = ImageStatus.ACTIVE,
            clickUrl = bannerData.clickUrl,
            deepLink = bannerData.deepLink,
            startDate = bannerData.startDate,
            endDate = bannerData.endDate,
            tags = bannerData.tags ?: emptyList(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedBanner = bannerRepository.save(banner)
        return convertToDTO(savedBanner)
    }

    @Cacheable(value = ["banners"], key = "#bannerId")
    fun getBannerById(bannerId: String): BannerDTO {
        val banner = bannerRepository.findByBannerId(bannerId)
            ?: throw RuntimeException("Banner not found with id: $bannerId")
        return convertToDTO(banner)
    }

    @CacheEvict(value = ["banners", "home_banners"], allEntries = true)
    fun updateBanner(bannerId: String, updateRequest: BannerUpdateDTO): BannerDTO {
        val existingBanner = bannerRepository.findByBannerId(bannerId)
            ?: throw RuntimeException("Banner not found with id: $bannerId")

        val updatedBanner = existingBanner.copy(
            title = updateRequest.title ?: existingBanner.title,
            description = updateRequest.description ?: existingBanner.description,
            shortDescription = updateRequest.shortDescription ?: existingBanner.shortDescription,
            bannerType = updateRequest.bannerType ?: existingBanner.bannerType,
            priority = updateRequest.priority ?: existingBanner.priority,
            isActive = updateRequest.isActive ?: existingBanner.isActive,
            clickUrl = updateRequest.clickUrl ?: existingBanner.clickUrl,
            deepLink = updateRequest.deepLink ?: existingBanner.deepLink,
            startDate = updateRequest.startDate ?: existingBanner.startDate,
            endDate = updateRequest.endDate ?: existingBanner.endDate,
            updatedAt = LocalDateTime.now()
        )

        val savedBanner = bannerRepository.save(updatedBanner)
        return convertToDTO(savedBanner)
    }

    @CacheEvict(value = ["banners", "home_banners"], allEntries = true)
    fun deleteBanner(bannerId: String) {
        val banner = bannerRepository.findByBannerId(bannerId)
            ?: throw RuntimeException("Banner not found with id: $bannerId")

        storageService.deleteBannerImages(banner.imageUrl, banner.thumbnailUrl)
        bannerRepository.delete(banner)
    }

    @Cacheable(value = ["home_banners"])
    fun getHomePageBanners(): List<BannerDTO> {
        val banners = bannerRepository.findByIsActiveTrueAndBannerTypeOrderByPriorityDesc(BannerType.HOME_PAGE)
        return banners.map { convertToDTO(it) }
    }

    fun getBannersBySuperCategory(superCategoryId: String, page: Int, size: Int): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority"))
        val bannerPage = bannerRepository.findBySuperCategoryId(superCategoryId, pageable)

        return PageResponse(
            content = bannerPage.content.map { convertToDTO(it) },
            page = bannerPage.number,
            size = bannerPage.size,
            totalElements = bannerPage.totalElements,
            totalPages = bannerPage.totalPages
        )
    }


    fun getBannersByCategory(categoryId: String, page: Int, size: Int): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority"))
        val bannerPage = bannerRepository.findByCategoryId(categoryId, pageable)

        return PageResponse(
            content = bannerPage.content.map { convertToDTO(it) },
            page = bannerPage.number,
            size = bannerPage.size,
            totalElements = bannerPage.totalElements,
            totalPages = bannerPage.totalPages
        )
    }

    fun searchBanners(query: String, page: Int, size: Int): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size)
        val bannerPage = bannerRepository.searchBanners(query, pageable)

        return PageResponse(
            content = bannerPage.content.map { convertToDTO(it) },
            page = bannerPage.number,
            size = bannerPage.size,
            totalElements = bannerPage.totalElements,
            totalPages = bannerPage.totalPages
        )
    }

    fun exportBannersToJson(superCategoryId: String?, categoryId: String?): String {
        val banners = when {
            superCategoryId != null -> bannerRepository.findBySuperCategoryId(superCategoryId)
            categoryId != null -> bannerRepository.findByCategoryId(categoryId)
            else -> bannerRepository.findAll()
        }

        return storageService.exportToJson(banners.map { convertToDTO(it) }, "banners_${System.currentTimeMillis()}.json")
    }

    fun populateBannersFromJson(jsonFile: MultipartFile): ImportResult {
        return storageService.importBannersFromJson(jsonFile) { bannerDTOs ->
            bannerDTOs.forEach { dto ->
                val banner = Banner(
                    bannerId = generateBannerId(),
                    title = dto.title,
                    description = dto.description,
                    shortDescription = dto.shortDescription,
                    superCategory = dto.superCategory,
                    category = dto.category,
                    subCategory = dto.subCategory,
                    imageUrl = dto.imageUrl,
                    thumbnailUrl = dto.thumbnailUrl,
                    mobileImageUrl = dto.mobileImageUrl,
                    tabletImageUrl = dto.tabletImageUrl,
                    imageMetadata = ImageMetadata(
                        originalFileName = "${dto.resourceName}.jpg",
                        fileSize = 0,
                        mimeType = "image/jpeg",
                        width = 0,
                        height = 0,
                        aspectRatio = 0.0,
                        colors = emptyList(),
                        dominantColor = null,
                        altText = dto.title,
                        title = dto.title,
                        compressionRatio = 0.0,
                        hash = UUID.randomUUID().toString()
                    ),
                    drawableResourceId = null,
                    resourceName = dto.resourceName,
                    resourcePath = dto.imageUrl,
                    bannerType = dto.bannerType,
                    priority = dto.priority,
                    isActive = dto.isActive,
                    status = ImageStatus.ACTIVE,
                    targetRoles = null,
                    targetLocations = null,
                    targetDevices = null,
                    startDate = null,
                    endDate = null,
                    clickUrl = dto.clickUrl,
                    deepLink = dto.deepLink,
                    actionType = null,
                    actionData = null,
                    clickCount = 0,
                    viewCount = 0,
                    ctr = 0.0,
                    tags = dto.tags,
                    metadata = emptyMap(),
                    version = 0,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    createdBy = null,
                    updatedBy = null
                )
                bannerRepository.save(banner)
            }
        }
    }

    fun recordBannerClick(bannerId: String) {
        bannerRepository.incrementClickCount(bannerId)
    }

    fun recordBannerView(bannerId: String) {
        bannerRepository.incrementViewCount(bannerId)
    }

    private fun generateBannerId(): String {
        return "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().substring(0, 8)}"
    }

    fun getAllBanners(): List<BannerDTO> {
        return bannerRepository.findAll().map { convertToDTO(it) }
    }

    fun getActiveBanners(): List<BannerDTO> {
        return bannerRepository.findByIsActiveTrue().map { convertToDTO(it) }
    }

    fun getBannersByType(bannerType: String): List<BannerDTO> {
        val type = BannerType.valueOf(bannerType.uppercase())
        return bannerRepository.findByBannerType(type).map { convertToDTO(it) }
    }

    private fun getSuperCategory(id: String): SuperCategory {
        return SuperCategory(
            id = id,
            name = when(id) {
                "FOOD_SUPER" -> "Food"
                "ELECTRONICS_SUPER" -> "Electronics"
                else -> "General"
            },
            code = id,
            description = "$id category",
            iconUrl = null
        )
    }

    private fun getCategory(id: String): Category {
        return Category(
            id = id,
            name = when(id) {
                "ALL_FOOD_CAT" -> "All Food"
                "HEALTHY_FOOD_CAT" -> "Healthy Food"
                else -> "General"
            },
            code = id,
            description = "$id items",
            iconUrl = null,
            displayOrder = 1
        )
    }

    private fun getSubCategory(id: String): SubCategory {
        return SubCategory(
            id = id,
            name = "Sub Category",
            code = id,
            description = "Sub category description",
            parentCategoryId = "ALL_FOOD_CAT"
        )
    }

    private fun convertToDTO(banner: Banner): BannerDTO {
        return BannerDTO(
            id = banner.id,
            bannerId = banner.bannerId,
            title = banner.title,
            description = banner.description,
            shortDescription = banner.shortDescription,
            superCategory = banner.superCategory,
            category = banner.category,
            subCategory = banner.subCategory,
            imageUrl = banner.imageUrl,
            thumbnailUrl = banner.thumbnailUrl,
            mobileImageUrl = banner.mobileImageUrl,
            tabletImageUrl = banner.tabletImageUrl,
            bannerType = banner.bannerType,
            priority = banner.priority,
            isActive = banner.isActive,
            resourceName = banner.resourceName,
            clickUrl = banner.clickUrl,
            deepLink = banner.deepLink,
            tags = banner.tags,
            viewCount = banner.viewCount,
            clickCount = banner.clickCount,
            ctr = banner.ctr
        )
    }
}