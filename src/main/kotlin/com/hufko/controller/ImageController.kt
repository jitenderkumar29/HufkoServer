package com.hufko.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream

@RestController
@RequestMapping("/api/images")
class ImageController {

    @GetMapping("/banners/{filename:.+}")
    fun getBannerImage(@PathVariable filename: String): ResponseEntity<InputStreamResource> {
        // Try multiple possible locations
        val possiblePaths = listOf(
            File("assets/banners/$filename"),
            File("src/main/resources/assets/banners/$filename"),
            File("../assets/banners/$filename"),
            File(System.getProperty("user.dir"), "assets/banners/$filename")
        )

        for (file in possiblePaths) {
            if (file.exists() && file.isFile) {
                println("Found image at: ${file.absolutePath}")
                val inputStream = FileInputStream(file)
                val resource = InputStreamResource(inputStream)

                // Determine content type based on file extension
                val contentType = when (file.extension.lowercase()) {
                    "png" -> MediaType.IMAGE_PNG
                    "jpg", "jpeg" -> MediaType.IMAGE_JPEG
                    "gif" -> MediaType.IMAGE_GIF
                    else -> MediaType.APPLICATION_OCTET_STREAM
                }

                return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(resource)
            }
        }

        println("Image not found: $filename")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
    }

    @GetMapping("/banners/list")
    fun listAvailableImages(): ResponseEntity<Map<String, Any>> {
        val possiblePaths = listOf(
            File("assets/banners"),
            File("src/main/resources/assets/banners")
        )

        val images = mutableListOf<String>()

        for (path in possiblePaths) {
            if (path.exists() && path.isDirectory) {
                images.addAll(path.listFiles()?.map { it.name } ?: emptyList())
            }
        }

        return ResponseEntity.ok(mapOf(
            "images" to images,
            "count" to images.size,
            "baseUrl" to "/api/images/banners/"
        ))
    }
}