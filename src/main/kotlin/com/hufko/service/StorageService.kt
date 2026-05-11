package com.hufko.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.hufko.dto.BannerDTO
import com.hufko.model.ImageMetadata
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO

@Service
class StorageService {

    @Value("\${storage.banner.path:./storage/banners/}")
    private lateinit var bannerStoragePath: String

    @Value("\${storage.json.path:./storage/jsondata/}")
    private lateinit var jsonStoragePath: String

    @Value("\${storage.profile.path:./storage/profiles/}")
    private lateinit var profileStoragePath: String

    @Value("\${storage.temp.path:./storage/temp/}")
    private lateinit var tempStoragePath: String

    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    fun storeBannerImage(file: MultipartFile, categoryId: String): ImageUrls {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val originalFilename = "${timestamp}_${file.originalFilename}"

        val roleFolder = determineRoleFolder(categoryId)
        val datePath = getDatePath()
        val targetDir = Paths.get("$bannerStoragePath/$roleFolder/$datePath")
        Files.createDirectories(targetDir)

        val originalPath = targetDir.resolve(originalFilename)
        file.transferTo(originalPath.toFile())

        val thumbnailFilename = "thumb_$originalFilename"
        val thumbnailPath = targetDir.resolve(thumbnailFilename)
        createThumbnail(originalPath, thumbnailPath, 300, 300)

        val mobileFilename = "mobile_$originalFilename"
        val mobilePath = targetDir.resolve(mobileFilename)
        createThumbnail(originalPath, mobilePath, 600, 800)

        val tabletFilename = "tablet_$originalFilename"
        val tabletPath = targetDir.resolve(tabletFilename)
        createThumbnail(originalPath, tabletPath, 1024, 768)

        val baseUrl = "/api/v1/storage/banners"
        return ImageUrls(
            originalUrl = "$baseUrl/$roleFolder/$datePath/$originalFilename",
            thumbnailUrl = "$baseUrl/$roleFolder/$datePath/$thumbnailFilename",
            mobileUrl = "$baseUrl/$roleFolder/$datePath/$mobileFilename",
            tabletUrl = "$baseUrl/$roleFolder/$datePath/$tabletFilename"
        )
    }

    fun extractImageMetadata(file: MultipartFile): ImageMetadata {
        val bufferedImage: BufferedImage = ImageIO.read(file.inputStream)
        val width = bufferedImage.width
        val height = bufferedImage.height
        val aspectRatio = width.toDouble() / height.toDouble()
        val hash = calculateHash(file.bytes)

        return ImageMetadata(
            originalFileName = file.originalFilename ?: "unknown",
            fileSize = file.size,
            mimeType = file.contentType ?: "image/jpeg",
            width = width,
            height = height,
            aspectRatio = aspectRatio,
            colors = emptyList(),
            dominantColor = null,
            altText = null,
            title = null,
            compressionRatio = 0.0,
            hash = hash
        )
    }

    fun exportToJson(data: Any, filename: String): String {
        val jsonDir = Paths.get("$jsonStoragePath/exports/${getDatePath()}")
        Files.createDirectories(jsonDir)

        val jsonFile = jsonDir.resolve(filename)
        objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(jsonFile.toFile(), data)

        return jsonFile.toAbsolutePath().toString()
    }

    fun importBannersFromJson(file: MultipartFile, processor: (List<BannerDTO>) -> Unit): ImportResult {
        val tempFile = Files.createTempFile("banner_import_", ".json")
        file.transferTo(tempFile.toFile())

        return try {
            val bannerDTOs: List<BannerDTO> = objectMapper.readValue(
                tempFile.toFile(),
                objectMapper.typeFactory.constructCollectionType(List::class.java, BannerDTO::class.java)
            )
            processor(bannerDTOs)
            ImportResult(
                importedCount = bannerDTOs.size,
                skippedCount = 0,
                errors = emptyList()
            )
        } catch (e: Exception) {
            ImportResult(
                importedCount = 0,
                skippedCount = 0,
                errors = listOf(e.message ?: "Unknown error")
            )
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

    private fun createThumbnail(source: Path, target: Path, width: Int, height: Int) {
        try {
            Thumbnails.of(source.toFile())
                .size(width, height)
                .outputQuality(0.8)
                .toFile(target.toFile())
        } catch (e: Exception) {
            try {
                Files.copy(source, target)
            } catch (ex: IOException) {
                println("Failed to create thumbnail: ${ex.message}")
            }
        }
    }

    private fun calculateHash(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun determineRoleFolder(categoryId: String): String {
        return when {
            categoryId.contains("premium", ignoreCase = true) -> "premium"
            categoryId.contains("admin", ignoreCase = true) -> "admin"
            else -> "user"
        }
    }

    private fun getDatePath(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    }

    fun deleteBannerImages(imageUrl: String, thumbnailUrl: String) {
        try {
            val imagePath = Paths.get(".", imageUrl.replace("/api/v1/storage/banners/", ""))
            val thumbnailPath = Paths.get(".", thumbnailUrl.replace("/api/v1/storage/banners/", ""))

            Files.deleteIfExists(imagePath)
            Files.deleteIfExists(thumbnailPath)

            val imageFileName = imagePath.fileName.toString()
            val parent = imagePath.parent

            val mobilePath = parent.resolve(imageFileName.replace(".", "_mobile."))
            val tabletPath = parent.resolve(imageFileName.replace(".", "_tablet."))
            Files.deleteIfExists(mobilePath)
            Files.deleteIfExists(tabletPath)

            println("Deleted banner images: ${imagePath.fileName}")
        } catch (e: Exception) {
            println("Error deleting banner images: ${e.message}")
        }
    }
}

data class ImageUrls(
    val originalUrl: String,
    val thumbnailUrl: String,
    val mobileUrl: String? = null,
    val tabletUrl: String? = null
)

data class ImportResult(
    val importedCount: Int,
    val skippedCount: Int,
    val errors: List<String>
)