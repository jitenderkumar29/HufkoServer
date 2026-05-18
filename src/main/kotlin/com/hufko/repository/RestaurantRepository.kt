package com.hufko.repository

import com.hufko.model.Restaurant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RestaurantRepository : MongoRepository<Restaurant, String> {

    // Find by restaurant ID
    fun findByRestaurantId(restaurantId: String): Restaurant?

    // Find by category (array contains)
    @Query("{ 'category': { \$in: [?0] }, 'isActive': true }")
    fun findByCategory(category: String): List<Restaurant>

    // Find by multiple categories
    @Query("{ 'category': { \$in: ?0 }, 'isActive': true }")
    fun findByCategories(categories: List<String>): List<Restaurant>

    // Find by topRated
    @Query("{ 'topRated': ?0, 'isActive': true }")
    fun findByTopRated(topRated: Boolean): List<Restaurant>

    // Find by category and topRated
    @Query("{ 'category': { \$in: [?0] }, 'topRated': ?1, 'isActive': true }")
    fun findByCategoryAndTopRated(category: String, topRated: Boolean): List<Restaurant>

    // Find all active restaurants with pagination
    @Query("{ 'isActive': true }")
    fun findAllActive(pageable: Pageable): Page<Restaurant>

    // Find all active restaurants
    @Query("{ 'isActive': true }")
    fun findAllActive(): List<Restaurant>

    // Search by restaurant name or title (case-insensitive)
    @Query("{ 'isActive': true, \$or: [ { 'restaurantName': { \$regex: ?0, \$options: 'i' } }, { 'title': { \$regex: ?0, \$options: 'i' } } ] }")
    fun searchRestaurants(searchTerm: String): List<Restaurant>

    // Find by rating greater than or equal
    @Query("{ 'rating': { \$gte: ?0 }, 'isActive': true }")
    fun findByRatingGreaterThanEqual(rating: String): List<Restaurant>

    // Find top rated with minimum rating
    @Query("{ 'topRated': true, 'rating': { \$gte: ?0 }, 'isActive': true }")
    fun findTopRatedWithMinRating(minRating: String): List<Restaurant>

    // Find by outlet
    @Query("{ 'outlet': ?0, 'isActive': true }")
    fun findByOutlet(outlet: String): List<Restaurant>

    // Find by cuisine type
    @Query("{ 'cuisineType': { \$in: [?0] }, 'isActive': true }")
    fun findByCuisineType(cuisineType: String): List<Restaurant>

    // Find pure veg restaurants
    @Query("{ 'isPureVeg': true, 'isActive': true }")
    fun findAllPureVeg(): List<Restaurant>

    // Find by price range
    @Query("{ 'isActive': true, 'priceAvg': { \$gte: ?0, \$lte: ?1 } }")
    fun findByPriceRange(minPrice: String, maxPrice: String): List<Restaurant>

    // Count by category
    @Query("{ 'category': { \$in: [?0] }, 'isActive': true }")
    fun countByCategory(category: String): Long

    // Find featured restaurants (top rated OR sponsored)
    @Query("{ 'isActive': true, \$or: [ { 'topRated': true }, { 'isSponsored': true } ] }")
    fun findFeaturedRestaurants(pageable: Pageable): Page<Restaurant>

    // Find by category and recommended
    @Query("{ 'category': { \$in: [?0] }, 'recommended': ?1, 'isActive': true }")
    fun findByCategoryAndRecommended(category: String, recommended: Boolean): List<Restaurant>

    // Find by category and featured
    @Query("{ 'category': { \$in: [?0] }, 'featured': ?1, 'isActive': true }")
    fun findByCategoryAndFeatured(category: String, featured: Boolean): List<Restaurant>

    // Find recommended restaurants
    @Query("{ 'recommended': true, 'isActive': true }")
    fun findAllRecommended(): List<Restaurant>

    // Find featured restaurants (if you want to override the existing one)
    @Query("{ 'featured': true, 'isActive': true }")
    fun findAllFeatured(): List<Restaurant>

    // Find by category, top rated, and recommended
    @Query("{ 'category': { \$in: [?0] }, 'topRated': ?1, 'recommended': ?2, 'isActive': true }")
    fun findByCategoryAndTopRatedAndRecommended(
        category: String,
        topRated: Boolean,
        recommended: Boolean
    ): List<Restaurant>

    @Query("{ 'featured': ?0, 'isActive': true }")
    fun findByFeatured(featured: Boolean, pageable: Pageable): Page<Restaurant>
}