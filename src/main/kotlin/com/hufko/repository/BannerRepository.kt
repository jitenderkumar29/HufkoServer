package com.hufko.repository

import com.hufko.enums.BannerType
import com.hufko.model.Banner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.stereotype.Repository

@Repository
interface BannerRepository : MongoRepository<Banner, String> {

    fun findByBannerId(bannerId: String): Banner?

    fun findByIsActiveTrue(): List<Banner>

    fun findByIsActiveTrueAndBannerTypeOrderByPriorityDesc(bannerType: BannerType): List<Banner>

    fun findByBannerType(bannerType: BannerType): List<Banner>

    // Paginated queries
    fun findBySuperCategoryId(superCategoryId: String, pageable: Pageable): Page<Banner>

    fun findByCategoryId(categoryId: String, pageable: Pageable): Page<Banner>

    fun findBySubCategoryId(subCategoryId: String, pageable: Pageable): Page<Banner>

    // Non-paginated queries
    fun findBySuperCategoryId(superCategoryId: String): List<Banner>

    fun findByCategoryId(categoryId: String): List<Banner>

    // Search queries
    @Query("{ 'title': { \$regex: ?0, \$options: 'i' } }")
    fun searchByTitle(titleRegex: String, pageable: Pageable): Page<Banner>

    @Query("{ \$or: [ { 'title': { \$regex: ?0, \$options: 'i' } }, { 'description': { \$regex: ?0, \$options: 'i' } }, { 'tags': { \$in: [?0] } } ] }")
    fun searchBanners(keyword: String, pageable: Pageable): Page<Banner>

    // Update methods
    @Query("{ 'bannerId': ?0 }")
    @Update("{ '\$inc': { 'clickCount': 1 } }")
    fun incrementClickCount(bannerId: String)

    @Query("{ 'bannerId': ?0 }")
    @Update("{ '\$inc': { 'viewCount': 1 } }")
    fun incrementViewCount(bannerId: String)
}