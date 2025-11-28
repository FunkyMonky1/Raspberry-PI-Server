plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.cloudserver"
version = "0.0.1-SNAPSHOT"
description = "pi"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web & optional Thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Spring Security für JWT / Auth / Role-Based Access
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Data JPA für persistente User/Rollen
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // PostgreSQL Driver
    runtimeOnly("org.postgresql:postgresql:42.7.7")

    // Flyway für Datenbank-Migrationen
    implementation("org.flywaydb:flyway-core:10.2.0")

    // JWT Token Handling
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") // JSON Parser für JWT

    // Lombok (optional, sehr praktisch für Getter/Setter/Constructors)
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

