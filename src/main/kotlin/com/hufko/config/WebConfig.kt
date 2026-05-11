package com.hufko.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Get the absolute path to assets folder
        val workingDir = System.getProperty("user.dir")
        val assetsPath = File(workingDir, "assets").absolutePath

        println("Serving static assets from: $assetsPath")

        registry.addResourceHandler("/assets/**")
            .addResourceLocations("file:$assetsPath/")
            .setCachePeriod(3600)
    }
}