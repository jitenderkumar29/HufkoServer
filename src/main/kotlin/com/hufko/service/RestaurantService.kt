package com.hufko.service

import com.hufko.dto.ApiResponse
import com.hufko.dto.FilterRequest
import com.hufko.model.Restaurant
import com.hufko.repository.RestaurantRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class RestaurantService(
    private val restaurantRepository: RestaurantRepository
) {

    // Get all active restaurants
    fun getAllActiveRestaurants(): List<Restaurant> {
        return restaurantRepository.findAllActive()
    }

    // Get by category
    fun getByCategory(category: String): List<Restaurant> {
        return restaurantRepository.findByCategory(category.uppercase())
    }

    // Get by multiple categories
    fun getByCategories(categories: List<String>): List<Restaurant> {
        return restaurantRepository.findByCategories(categories.map { it.uppercase() })
    }

    // Get by top rated
    fun getByTopRated(topRated: Boolean): List<Restaurant> {
        return restaurantRepository.findByTopRated(topRated)
    }

    // Get by category and top rated
    fun getByCategoryAndTopRated(category: String, topRated: Boolean): List<Restaurant> {
        return restaurantRepository.findByCategoryAndTopRated(category.uppercase(), topRated)
    }

    // Get by ID
    fun getRestaurantById(id: String): Restaurant? {
        return restaurantRepository.findById(id).orElse(null)
    }

    // Get by restaurant ID
    fun getRestaurantByRestaurantId(restaurantId: String): Restaurant? {
        return restaurantRepository.findByRestaurantId(restaurantId)
    }

    // Search restaurants
    fun searchRestaurants(searchTerm: String): List<Restaurant> {
        return restaurantRepository.searchRestaurants(searchTerm)
    }

    // Get by rating
    fun getByRating(minRating: String): List<Restaurant> {
        return restaurantRepository.findByRatingGreaterThanEqual(minRating)
    }

    // Get top rated with minimum rating
    fun getTopRatedWithMinRating(minRating: String): List<Restaurant> {
        return restaurantRepository.findTopRatedWithMinRating(minRating)
    }

    // Get by outlet
    fun getByOutlet(outlet: String): List<Restaurant> {
        return restaurantRepository.findByOutlet(outlet)
    }

    // Get by cuisine type
    fun getByCuisineType(cuisineType: String): List<Restaurant> {
        return restaurantRepository.findByCuisineType(cuisineType)
    }

    // Get pure veg restaurants
    fun getPureVegRestaurants(): List<Restaurant> {
        return restaurantRepository.findAllPureVeg()
    }

    // Advanced filter
    fun advancedFilter(filterRequest: FilterRequest): List<Restaurant> {
        var restaurants = mutableListOf<Restaurant>()

        when {
            !filterRequest.searchTerm.isNullOrEmpty() -> {
                restaurants = searchRestaurants(filterRequest.searchTerm).toMutableList()
            }
            !filterRequest.category.isNullOrEmpty() -> {
                restaurants = getByCategories(filterRequest.category).toMutableList()
            }
            filterRequest.topRated != null -> {
                restaurants = getByTopRated(filterRequest.topRated).toMutableList()
            }
            filterRequest.recommended != null -> {  // Add this
                restaurants = if (filterRequest.recommended)
                    getAllRecommendedRestaurants().toMutableList()
                else
                    getAllActiveRestaurants().filter { !it.recommended }.toMutableList()
            }
            filterRequest.featured != null -> {  // Add this
                restaurants = if (filterRequest.featured)
                    getAllFeaturedRestaurants().toMutableList()
                else
                    getAllActiveRestaurants().filter { !it.featured }.toMutableList()
            }
            !filterRequest.outlet.isNullOrEmpty() -> {
                restaurants = getByOutlet(filterRequest.outlet).toMutableList()
            }
            !filterRequest.cuisineType.isNullOrEmpty() -> {
                restaurants = getByCuisineType(filterRequest.cuisineType).toMutableList()
            }
            filterRequest.pureVeg == true -> {
                restaurants = getPureVegRestaurants().toMutableList()
            }
            !filterRequest.minRating.isNullOrEmpty() -> {
                restaurants = getByRating(filterRequest.minRating).toMutableList()
            }
            else -> {
                restaurants = getAllActiveRestaurants().toMutableList()
            }
        }

        // Apply additional filters
        if (filterRequest.topRated != null && !filterRequest.category.isNullOrEmpty()) {
            restaurants = getByCategoryAndTopRated(
                filterRequest.category!![0],
                filterRequest.topRated
            ).toMutableList()
        }

        if (filterRequest.recommended != null && !filterRequest.category.isNullOrEmpty()) {
            restaurants = getByCategoryAndRecommended(
                filterRequest.category!![0],
                filterRequest.recommended
            ).toMutableList()
        }

        if (filterRequest.featured != null && !filterRequest.category.isNullOrEmpty()) {
            restaurants = getByCategoryAndFeatured(
                filterRequest.category!![0],
                filterRequest.featured
            ).toMutableList()
        }

        if (!filterRequest.minRating.isNullOrEmpty() && filterRequest.topRated == true) {
            restaurants = getTopRatedWithMinRating(filterRequest.minRating).toMutableList()
        }

        return restaurants
    }

    // Get with pagination
    fun getRestaurantsWithPagination(page: Int, size: Int): org.springframework.data.domain.Page<Restaurant> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"))
        return restaurantRepository.findAllActive(pageable)
    }

    // Get featured restaurants
//    fun getFeaturedRestaurants(page: Int, size: Int): org.springframework.data.domain.Page<Restaurant> {
//        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"))
//        return restaurantRepository.findFeaturedRestaurants(pageable)
//    }

    // Update existing featured method to use the 'featured' field
    fun getFeaturedRestaurants(page: Int, size: Int): org.springframework.data.domain.Page<Restaurant> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"))
        // Change this to use the featured field
        return restaurantRepository.findByFeatured(true, pageable)
    }

    // Count by category
    fun countByCategory(category: String): Long {
        return restaurantRepository.countByCategory(category.uppercase())
    }

    // Get by category and recommended
    fun getByCategoryAndRecommended(category: String, recommended: Boolean): List<Restaurant> {
        return restaurantRepository.findByCategoryAndRecommended(category.uppercase(), recommended)
    }

    // Get by category and featured
    fun getByCategoryAndFeatured(category: String, featured: Boolean): List<Restaurant> {
        return restaurantRepository.findByCategoryAndFeatured(category.uppercase(), featured)
    }

    // Get all recommended restaurants
    fun getAllRecommendedRestaurants(): List<Restaurant> {
        return restaurantRepository.findAllRecommended()
    }

    // Get all featured restaurants
    fun getAllFeaturedRestaurants(): List<Restaurant> {
        return restaurantRepository.findAllFeatured()
    }

    // Get by category, top rated, and recommended
    fun getByCategoryAndTopRatedAndRecommended(
        category: String,
        topRated: Boolean,
        recommended: Boolean
    ): List<Restaurant> {
        return restaurantRepository.findByCategoryAndTopRatedAndRecommended(
            category.uppercase(),
            topRated,
            recommended
        )
    }


}