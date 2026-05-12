package com.hufko.controller

import com.hufko.dto.BannerDTO
import com.hufko.dto.PageResponse
import com.hufko.service.BannerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/banners")
class BannerController(
    private val bannerService: BannerService
) {

    // ================= BASIC APIs =================

    @GetMapping
    fun getAllBanners(): ResponseEntity<List<BannerDTO>> {
        return ResponseEntity.ok(bannerService.getAllBanners())
    }

    @GetMapping("/active")
    fun getActiveBanners(): ResponseEntity<List<BannerDTO>> {
        return ResponseEntity.ok(bannerService.getActiveBanners())
    }

    @GetMapping("/home")
    fun getHomeBanners(): ResponseEntity<List<BannerDTO>> {
        return ResponseEntity.ok(bannerService.getHomePageBanners())
    }

    @GetMapping("/{bannerId}")
    fun getBannerById(@PathVariable bannerId: String): ResponseEntity<BannerDTO> {
        return ResponseEntity.ok(bannerService.getBannerById(bannerId))
    }

    @GetMapping("/type/{bannerType}")
    fun getBannersByType(@PathVariable bannerType: String): ResponseEntity<List<BannerDTO>> {
        return ResponseEntity.ok(bannerService.getBannersByType(bannerType))
    }

    // ================= CATEGORY HIERARCHY APIs =================

    @GetMapping("/supercategory/{superCategoryId}")
    fun getBannersBySuperCategory(
        @PathVariable superCategoryId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<BannerDTO>> {
        return ResponseEntity.ok(
            bannerService.getBannersBySuperCategory(superCategoryId, page, size)
        )
    }

    @GetMapping("/supercategory/{superCategoryId}/category/{categoryId}")
    fun getBannersBySuperCategoryAndCategory(
        @PathVariable superCategoryId: String,
        @PathVariable categoryId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<BannerDTO>> {
        return ResponseEntity.ok(
            bannerService.getBannersBySuperCategoryAndCategory(
                superCategoryId, categoryId, page, size
            )
        )
    }

    @GetMapping("/supercategory/{superCategoryId}/category/{categoryId}/subcategory/{subCategoryId}")
    fun getBannersBySuperCategoryAndCategoryAndSubCategory(
        @PathVariable superCategoryId: String,
        @PathVariable categoryId: String,
        @PathVariable subCategoryId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<BannerDTO>> {
        return ResponseEntity.ok(
            bannerService.getBannersBySuperCategoryAndCategoryAndSubCategory(
                superCategoryId, categoryId, subCategoryId, page, size
            )
        )
    }

    // ================= LEGACY / SIMPLE FILTER APIs =================

    @GetMapping("/category/{categoryId}")
    fun getBannersByCategory(
        @PathVariable categoryId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<BannerDTO>> {
        return ResponseEntity.ok(
            bannerService.getBannersByCategory(categoryId, page, size)
        )
    }

    @GetMapping("/subcategory/{subCategoryId}")
    fun getBannersBySubCategory(
        @PathVariable subCategoryId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<BannerDTO>> {
        return ResponseEntity.ok(
            bannerService.getBannersBySubCategory(subCategoryId, page, size)
        )
    }

    // ================= SEARCH =================

    @GetMapping("/search")
    fun searchBanners(
        @RequestParam q: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<BannerDTO>> {
        return ResponseEntity.ok(
            bannerService.searchBanners(q, page, size)
        )
    }

    // ================= ANALYTICS =================

    @PostMapping("/{bannerId}/click")
    fun trackClick(@PathVariable bannerId: String): ResponseEntity<Map<String, String>> {
        bannerService.recordBannerClick(bannerId)
        return ResponseEntity.ok(mapOf("message" to "Click tracked successfully"))
    }

    @PostMapping("/{bannerId}/view")
    fun trackView(@PathVariable bannerId: String): ResponseEntity<Map<String, String>> {
        bannerService.recordBannerView(bannerId)
        return ResponseEntity.ok(mapOf("message" to "View tracked successfully"))
    }
}