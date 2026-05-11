package com.hufko.config

import org.springframework.boot.CommandLineRunner
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Component
class MongoDatabaseChecker(
    private val mongoTemplate: MongoTemplate
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val dbName = mongoTemplate.db.name
        println("\n========================================")
        println("✅ Connected to MongoDB Database: $dbName")
        println("========================================\n")

        // Create a test collection to ensure database is created
        val collectionName = "init_check"
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName)
            println("📦 Database '$dbName' initialized successfully")
            mongoTemplate.dropCollection(collectionName)
        }
    }
}