package com.hufko.service

import com.hufko.dto.*
import com.hufko.enums.BannerType
import com.hufko.enums.ImageStatus
import com.hufko.model.Banner
import com.hufko.model.ImageMetadata
import com.hufko.repository.BannerRepository
import com.hufko.repository.CategoryRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
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

    // ================= CREATE =================

    @CacheEvict(value = ["banners"], allEntries = true)
    fun createBanner(file: MultipartFile, bannerData: BannerCreateDTO): BannerDTO {

        val imageUrls = storageService.storeBannerImage(file, bannerData.categoryId)
        val metadata = storageService.extractImageMetadata(file)

        val banner = Banner(
            bannerId = generateBannerId(),
            title = bannerData.title,
            description = bannerData.description,
            shortDescription = bannerData.shortDescription,

            // IMPORTANT: STRING IDS (FIXED MODEL STYLE)
            superCategory = bannerData.superCategoryId,
            category = bannerData.categoryId,
            subCategory = bannerData.subCategoryId,

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

            tags = bannerData.tags,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return convertToDTO(bannerRepository.save(banner))
    }

    // ================= READ =================

    fun getAllBanners(): List<BannerDTO> =
        bannerRepository.findAll().map { convertToDTO(it) }

    fun getActiveBanners(): List<BannerDTO> =
        bannerRepository.findByIsActiveTrue().map { convertToDTO(it) }

    @Cacheable("home_banners")
    fun getHomePageBanners(): List<BannerDTO> =
        bannerRepository
            .findByIsActiveTrueAndBannerTypeOrderByPriorityDesc(BannerType.HOME_PAGE)
            .map { convertToDTO(it) }

    fun getBannerById(bannerId: String): BannerDTO =
        convertToDTO(
            bannerRepository.findByBannerId(bannerId)
                ?: throw RuntimeException("Banner not found: $bannerId")
        )

    fun getBannersByType(bannerType: String): List<BannerDTO> {
        val type = BannerType.valueOf(bannerType.uppercase())
        return bannerRepository.findByBannerType(type).map { convertToDTO(it) }
    }

    // ================= HIERARCHY (FIXED - NO *Id METHODS*) =================

    fun getBannersBySuperCategory(superCategoryId: String, page: Int, size: Int): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority"))
        val pageData = bannerRepository.findBySuperCategory(superCategoryId, pageable)

        return toPageResponse(pageData)
    }

    fun getBannersByCategory(categoryId: String, page: Int, size: Int): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority"))
        val pageData = bannerRepository.findByCategory(categoryId, pageable)

        return toPageResponse(pageData)
    }

    fun getBannersBySubCategory(subCategoryId: String, page: Int, size: Int): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority"))
        val pageData = bannerRepository.findBySubCategory(subCategoryId, pageable)

        return toPageResponse(pageData)
    }

    fun getBannersBySuperCategoryAndCategory(
        superCategoryId: String,
        categoryId: String,
        page: Int,
        size: Int
    ): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority"))
        val pageData = bannerRepository.findBySuperCategoryAndCategory(
            superCategoryId, categoryId, pageable
        )

        return toPageResponse(pageData)
    }

    fun getBannersBySuperCategoryAndCategoryAndSubCategory(
        superCategoryId: String,
        categoryId: String,
        subCategoryId: String,
        page: Int,
        size: Int
    ): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority"))
        val pageData = bannerRepository.findBySuperCategoryAndCategoryAndSubCategory(
            superCategoryId, categoryId, subCategoryId, pageable
        )

        return toPageResponse(pageData)
    }

    // ================= SEARCH =================

    fun searchBanners(query: String, page: Int, size: Int): PageResponse<BannerDTO> {
        val pageable = PageRequest.of(page, size)
        val pageData = bannerRepository.searchBanners(query, pageable)
        return toPageResponse(pageData)
    }

    // ================= TRACKING =================

    fun recordBannerClick(bannerId: String) {
        bannerRepository.incrementClickCount(bannerId)
    }

    fun recordBannerView(bannerId: String) {
        bannerRepository.incrementViewCount(bannerId)
    }

    // ================= HELPERS =================

    private fun toPageResponse(page: Page<Banner>): PageResponse<BannerDTO> {
        return PageResponse(
            content = page.content.map { convertToDTO(it) },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages
        )
    }

    private fun generateBannerId(): String =
        "BNR_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"

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