plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.hufko"
version = "0.0.1-SNAPSHOT"
description = "HufkoServer"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Jakarta Validation API
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // Optional: Hibernate Validator (implementation)
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")

    // Required for EL expressions (used by Hibernate Validator)
    implementation("org.glassfish:jakarta.el:4.0.2")

	// Spring Data MongoDB
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	// Spring Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Optional: For method security (@PreAuthorize, etc.)
	implementation("org.springframework.security:spring-security-core")

	// Jackson Kotlin module - this is essential for jacksonObjectMapper()
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Your other dependencies...
	implementation("org.springframework.boot:spring-boot-starter-web")

	// JJWT - Java JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") // or jjwt-gson if you prefer Gson

	// If you're using Kotlin, you might also need
	implementation("io.jsonwebtoken:jjwt-orgjson:0.11.5") // optional, for org.json support

	// Jackson JSR310 module for Java 8 time support (LocalDateTime, etc.)
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.3")

	// Or use Spring Boot's built-in Jackson (often includes this)
	// implementation("org.springframework.boot:spring-boot-starter-json")

	// Image processing library (Thumbnails)
	implementation("net.coobird:thumbnailator:0.4.19")

	// Alternative: If you need more advanced image processing
	// implementation("org.imgscalr:imgscalr-lib:4.2")

	// For image metadata extraction
	implementation("com.drewnoakes:metadata-extractor:2.19.0") // optional

	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
