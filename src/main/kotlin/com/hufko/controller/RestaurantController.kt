package com.hufko.controller

import com.hufko.dto.ApiResponse
import com.hufko.dto.FilterRequest
import com.hufko.service.RestaurantService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = ["*"])
class RestaurantController(
    private val restaurantService: RestaurantService
) {

    // Get all active restaurants
    @GetMapping("/all")
    fun getAllRestaurants(): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getAllActiveRestaurants()
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size
            )
        )
    }

    // Get by category
    @GetMapping("/category/{category}")
    fun getRestaurantsByCategory(@PathVariable category: String): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByCategory(category)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = "Found ${restaurants.size} restaurants in category: $category"
            )
        )
    }

    // Get by top rated
    @GetMapping("/top-rated")
    fun getTopRatedRestaurants(
        @RequestParam(defaultValue = "true") topRated: Boolean
    ): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByTopRated(topRated)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = if (topRated) "Top rated restaurants" else "Non top-rated restaurants"
            )
        )
    }

    // Get by category and top rated
    @GetMapping("/filter")
    fun getRestaurantsByFilter(
        @RequestParam category: String,
        @RequestParam(defaultValue = "false") topRated: Boolean
    ): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByCategoryAndTopRated(category, topRated)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size
            )
        )
    }

    // Advanced filter with POST
    @PostMapping("/filter/advanced")
    fun advancedFilter(@RequestBody filterRequest: FilterRequest): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.advancedFilter(filterRequest)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size
            )
        )
    }

    // Get by ID
    @GetMapping("/{id}")
    fun getRestaurantById(@PathVariable id: String): ResponseEntity<ApiResponse<Any>> {
        val restaurant = restaurantService.getRestaurantById(id)
        return if (restaurant != null) {
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = restaurant
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    success = false,
                    message = "Restaurant not found with id: $id"
                )
            )
        }
    }

    // Get by restaurant ID
    @GetMapping("/restaurant-id/{restaurantId}")
    fun getRestaurantByRestaurantId(@PathVariable restaurantId: String): ResponseEntity<ApiResponse<Any>> {
        val restaurant = restaurantService.getRestaurantByRestaurantId(restaurantId)
        return if (restaurant != null) {
            ResponseEntity.ok(
                ApiResponse(
                    success = true,
                    data = restaurant
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    success = false,
                    message = "Restaurant not found with restaurantId: $restaurantId"
                )
            )
        }
    }

    // Search restaurants
    @GetMapping("/search")
    fun searchRestaurants(@RequestParam q: String): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.searchRestaurants(q)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = "Found ${restaurants.size} restaurants matching '$q'"
            )
        )
    }

    // Get by rating
    @GetMapping("/rating/{minRating}")
    fun getRestaurantsByRating(@PathVariable minRating: String): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByRating(minRating)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = "Restaurants with rating >= $minRating"
            )
        )
    }

    // Get top rated with minimum rating
    @GetMapping("/top-rated/min-rating/{minRating}")
    fun getTopRatedWithMinRating(@PathVariable minRating: String): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getTopRatedWithMinRating(minRating)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size
            )
        )
    }

    // Get by outlet
    @GetMapping("/outlet/{outlet}")
    fun getRestaurantsByOutlet(@PathVariable outlet: String): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByOutlet(outlet)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size
            )
        )
    }

    // Get by cuisine type
    @GetMapping("/cuisine/{cuisineType}")
    fun getRestaurantsByCuisine(@PathVariable cuisineType: String): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByCuisineType(cuisineType)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size
            )
        )
    }

    // Get pure veg restaurants
    @GetMapping("/pure-veg")
    fun getPureVegRestaurants(): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getPureVegRestaurants()
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = "Pure vegetarian restaurants"
            )
        )
    }

    // Get with pagination
    @GetMapping("/pagination")
    fun getRestaurantsWithPagination(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<Any>> {
        val pageData = restaurantService.getRestaurantsWithPagination(page, size)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = pageData.content,
                count = pageData.content.size,
                page = pageData.number,
                totalPages = pageData.totalPages,
                totalElements = pageData.totalElements
            )
        )
    }

    // Get featured restaurants
    @GetMapping("/featured")
    fun getFeaturedRestaurants(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<Any>> {
        val pageData = restaurantService.getFeaturedRestaurants(page, size)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = pageData.content,
                count = pageData.content.size,
                page = pageData.number,
                totalPages = pageData.totalPages,
                totalElements = pageData.totalElements,
                message = "Featured restaurants (top rated or sponsored)"
            )
        )
    }

    // Get categories summary
    @GetMapping("/categories/summary")
    fun getCategoriesSummary(): ResponseEntity<ApiResponse<Any>> {
        val categories = listOf("ALL", "VEG", "NON-VEG", "CHINESE", "NORTH_INDIAN", "ITALIAN")
        val summary = categories.associateWith { category ->
            restaurantService.countByCategory(category)
        }
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = summary,
                message = "Category wise restaurant count"
            )
        )
    }

    @GetMapping("/category/{category}/recommended")
    fun getRestaurantsByCategoryAndRecommended(
        @PathVariable category: String,
        @RequestParam(defaultValue = "true") recommended: Boolean
    ): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByCategoryAndRecommended(category, recommended)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = if (recommended)
                    "Recommended restaurants in category: $category"
                else
                    "Non-recommended restaurants in category: $category"
            )
        )
    }

    // Get by category and featured
    @GetMapping("/category/{category}/featured")
    fun getRestaurantsByCategoryAndFeatured(
        @PathVariable category: String,
        @RequestParam(defaultValue = "true") featured: Boolean
    ): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByCategoryAndFeatured(category, featured)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = if (featured)
                    "Featured restaurants in category: $category"
                else
                    "Non-featured restaurants in category: $category"
            )
        )
    }

    // Get all recommended restaurants
    @GetMapping("/recommended")
    fun getAllRecommendedRestaurants(): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getAllRecommendedRestaurants()
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = "Recommended restaurants"
            )
        )
    }

    // Get all featured restaurants (override existing or create new)
    @GetMapping("/featured/all")
    fun getAllFeaturedRestaurants(): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getAllFeaturedRestaurants()
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = "Featured restaurants"
            )
        )
    }

    // Get by category, top rated, and recommended (advanced filter)
    @GetMapping("/filter/advanced/recommended")
    fun getRestaurantsByCategoryTopRatedAndRecommended(
        @RequestParam category: String,
        @RequestParam(defaultValue = "false") topRated: Boolean,
        @RequestParam(defaultValue = "true") recommended: Boolean
    ): ResponseEntity<ApiResponse<Any>> {
        val restaurants = restaurantService.getByCategoryAndTopRatedAndRecommended(category, topRated, recommended)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = restaurants,
                count = restaurants.size,
                message = "Found ${restaurants.size} restaurants matching criteria"
            )
        )
    }
}
