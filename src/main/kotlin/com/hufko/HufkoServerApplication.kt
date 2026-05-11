package com.hufko

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = ["com.hufko.repository"])
@RestController
class HufkoServerApplication {

    @GetMapping("/")
    fun home(): String {
        return "Hufko Server is running!"
    }

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "timestamp" to System.currentTimeMillis().toString(),
            "service" to "hufko-server",
            "database" to "MongoDB"
        )
    }
}

fun main(args: Array<String>) {
    runApplication<HufkoServerApplication>(*args)
}