# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A self-hosted personal cloud server built with Java 17 + Spring Boot 3.5.7, designed to run on a Raspberry Pi. Users upload, organize, and download files (university materials) by category through a Thymeleaf UI, secured behind Spring Security login. Single admin user; no database-backed user table yet.

## Tech Stack

- **Java 17**, **Spring Boot 3.5.7**, **Gradle 8 (Kotlin DSL)**
- **Lombok** for getters/setters on JPA entities
- **Spring Security** (form login, BCrypt, in-memory user)
- **Spring Data JPA** + **MySQL 8** (production) / **H2** (tests only)
- **Thymeleaf** for server-rendered UI

## Commands

```bash
# Run locally (requires application-local.properties — see below)
./gradlew bootRun --args='--spring.profiles.active=local'

# Run all tests (uses H2 in-memory DB, no local setup needed)
./gradlew test

# Build production jar
./gradlew bootJar

# Docker (local or Pi deployment)
docker-compose up --build
```

## Local Development Setup

Create `src/main/resources/application-local.properties` (gitignored):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cloud_storage?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
app.admin.username=YOUR_USERNAME
app.admin.password=YOUR_PASSWORD
storage.location=./server_files
```

For Docker, copy `.env.example` to `.env` and fill in credentials. App runs on port 8088.

## Architecture

**Single controller, filesystem + DB split:** `FileUploadController` handles all three operations — list (`GET /`), upload (`POST /`), download (`GET /files/download/{id}`). Actual file bytes go to the filesystem; only metadata (filename, uploader, IP, category, path, size, upload date) is persisted to MySQL via JPA (`FileMetadata` entity / `FileMetadataRepository`).

**Storage layout:** Files are stored under `storage.location/{CATEGORY_NAME}/{UUID}_{originalFilename}`. `FileSystemStorageService` enforces a path-traversal guard — stored paths must stay inside `rootLocation`. `FileCategory` is a Java enum; adding a new category requires only adding an enum value.

**Current categories (`FileCategory` enum):** `DATA_SCIENCE`, `PROGRAMMING_INTERNSHIP`, `MATH`, `ALDAT`, `THEORETIC_COMPUTER_SCIENCE`

**Allowed file extensions:** `pdf`, `png`, `jpg`, `jpeg`, `txt`, `docx`, `xlsx`, `pptx`, `zip` (enforced in `FileUploadController`). Max upload size: **100 MB**.

**Auth:** Single admin user configured via env vars (`ADMIN_USER` / `ADMIN_PASSWORD`) in `SecurityConfig`. Credentials are BCrypt-encoded at startup and held in-memory. The user is assigned role `USER` (not `ADMIN`). CSRF protection is currently **disabled** — worth re-enabling for production.

**IP detection:** `X-Forwarded-For` header is checked first (proxy-aware), falling back to `request.getRemoteAddr()`.

**Config profiles:** `application.properties` uses `${ENV_VAR:default}` placeholders for Docker. The `local` profile overrides these via `application-local.properties`.

**Docker:** Multi-stage build — Gradle + JDK 17 for the build stage, `eclipse-temurin:17-jre` for the runtime image. App runs as a non-root `appuser`. JVM flags: `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75` (Pi-friendly).

**Tests:** Two test classes, both under `src/test/`:
- `FileUploadControllerTest` — `@SpringBootTest` + `@AutoConfigureMockMvc` + H2: integration tests covering successful upload, blocked extension, and unauthenticated redirect.
- `FileSystemStorageServiceTest` — Mockito unit tests using `@TempDir`: covers metadata persistence, empty file rejection, missing file load, and path-traversal attack prevention.

## Key Files

| Path | Purpose |
|------|---------|
| `src/main/java/com/cloudserver/pi/SecurityConfig.java` | Spring Security — auth, route protection, CSRF config |
| `src/main/java/com/cloudserver/pi/uploadingfiles/FileUploadController.java` | All HTTP endpoints; allowed extension list |
| `src/main/java/com/cloudserver/pi/uploadingfiles/FileSystemStorageService.java` | File I/O, path-traversal guard, metadata persistence |
| `src/main/java/com/cloudserver/pi/uploadingfiles/StorageService.java` | Interface for storage operations |
| `src/main/java/com/cloudserver/pi/uploadingfiles/StorageProperties.java` | `@ConfigurationProperties("storage")` — binds `storage.location` |
| `src/main/java/com/cloudserver/pi/model/FileCategory.java` | Enum of valid upload categories |
| `src/main/java/com/cloudserver/pi/model/FileMetadata.java` | JPA entity — file metadata persisted to MySQL |
| `src/main/java/com/cloudserver/pi/uploadingfiles/FileMetadataRepository.java` | Spring Data JPA repository for `FileMetadata` |
| `src/main/resources/application.properties` | Main config with env var placeholders |
| `src/main/resources/schema.sql` | Reference DDL for the `file_metadata` table |
| `src/main/resources/templates/uploadForm.html` | Single Thymeleaf template for the entire UI |
| `src/main/resources/static/css/style.css` | UI styles |
| `docker-compose.yml` | MySQL + app services; uses named volumes for data persistence |
| `Dockerfile` | Multi-stage build; runs app as non-root user |
