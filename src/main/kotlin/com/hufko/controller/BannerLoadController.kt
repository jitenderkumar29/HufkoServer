package com.hufko.controller

import com.hufko.service.BatchBannerLoader
import com.hufko.service.BannerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/banners")
class BannerLoadController(
    private val batchBannerLoader: BatchBannerLoader,
    private val bannerService: BannerService
) {

    @PostMapping("/load-from-assets")
    fun loadBannersFromAssets(): ResponseEntity<Map<String, Any>> {
        return try {
            // Run the batch loader directly
            batchBannerLoader.run()

            ResponseEntity.ok(mapOf(
                "success" to true,
                "message" to "Banners loaded successfully from assets folder",
                "totalBanners" to bannerService.getAllBanners().size
            ))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf(
                "success" to false,
                "message" to "Failed to load banners: ${e.message}"
            ))
        }
    }

    @GetMapping("/count")
    fun getBannerCount(): ResponseEntity<Map<String, Any>> {
        val count = bannerService.getAllBanners().size
        return ResponseEntity.ok(mapOf(
            "totalBanners" to count
        ))
    }
}