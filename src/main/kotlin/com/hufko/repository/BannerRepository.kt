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

    // ========== PAGINATED CATEGORY QUERIES ==========

    fun findBySuperCategory(superCategory: String, pageable: Pageable): Page<Banner>

    fun findByCategory(category: String, pageable: Pageable): Page<Banner>

    fun findBySubCategory(subCategory: String?, pageable: Pageable): Page<Banner>

    // ========== NON-PAGINATED ==========

    fun findBySuperCategory(superCategory: String): List<Banner>

    fun findByCategory(category: String): List<Banner>

    fun findBySubCategory(subCategory: String?): List<Banner>

    // ========== COMBINED FILTERS ==========

    fun findBySuperCategoryAndCategory(
        superCategory: String,
        category: String,
        pageable: Pageable
    ): Page<Banner>

    fun findBySuperCategoryAndCategoryAndSubCategory(
        superCategory: String,
        category: String,
        subCategory: String?,
        pageable: Pageable
    ): Page<Banner>

    // ========== SEARCH ==========

    @Query("{ 'title': { \$regex: ?0, \$options: 'i' } }")
    fun searchByTitle(titleRegex: String, pageable: Pageable): Page<Banner>

    @Query("{ \$or: [ { 'title': { \$regex: ?0, \$options: 'i' } }, { 'description': { \$regex: ?0, \$options: 'i' } }, { 'tags': { \$regex: ?0, \$options: 'i' } } ] }")
    fun searchBanners(keyword: String, pageable: Pageable): Page<Banner>

    // ========== TRACKING ==========

    @Query("{ 'bannerId': ?0 }")
    @Update("{ '\$inc': { 'clickCount': 1 } }")
    fun incrementClickCount(bannerId: String)

    @Query("{ 'bannerId': ?0 }")
    @Update("{ '\$inc': { 'viewCount': 1 } }")
    fun incrementViewCount(bannerId: String)
}