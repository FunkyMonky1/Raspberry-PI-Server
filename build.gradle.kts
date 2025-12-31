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

   
    runtimeOnly("org.postgresql:postgresql:42.7.7")

    // H2 In-Memory Datenbank (für Tests / Upload-Projekt)
    runtimeOnly("com.h2database:h2")


    

    // JWT Token Handling
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") 

    // Lombok (optional, sehr praktisch für Getter/Setter/Constructors)
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("com.mysql:mysql-connector-j")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

