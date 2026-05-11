package com.hufko.repository

import com.hufko.model.CategoryHierarchy
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : MongoRepository<CategoryHierarchy, String> {
    fun findByCode(code: String): CategoryHierarchy?
    fun findByParentId(parentId: String): List<CategoryHierarchy>
    fun findByLevel(level: Int): List<CategoryHierarchy>
    fun findByPathStartingWith(path: String): List<CategoryHierarchy>
}