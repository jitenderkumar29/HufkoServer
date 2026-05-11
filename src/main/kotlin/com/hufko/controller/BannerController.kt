package com.hufko.controller

import com.hufko.dto.BannerDTO
import com.hufko.service.BannerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/banners")
class BannerController(
    private val bannerService: BannerService
) {

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