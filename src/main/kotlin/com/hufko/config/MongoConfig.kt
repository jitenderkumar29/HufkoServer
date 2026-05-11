package com.hufko.config


import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.beans.factory.annotation.Value

@Configuration
class MongoConfig {

    @Value("\${spring.data.mongodb.host:localhost}")
    private lateinit var host: String

    @Value("\${spring.data.mongodb.port:27017}")
    private var port: Int = 27017

    @Value("\${spring.data.mongodb.database:hufko_db}")
    private lateinit var database: String

    @Bean
    fun mongoTemplate(): MongoTemplate {
        val connectionString = "mongodb://$host:$port/$database"
        println("Connecting to MongoDB at: $connectionString")
        val client = MongoClients.create(connectionString)
        return MongoTemplate(client, database)
    }
}