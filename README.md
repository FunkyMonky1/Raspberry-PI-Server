# Raspberry-PI-Server
A self-hosted personal cloud server built with Java Spring Boot, running on a Raspberry Pi. 
Upload, organize, and download university materials by category — secured behind authentication.

# Requirements:
Java 17 -older Raspberry Pi's only support this version 

MySQL -database for storing file metadata

Gradle -build tool (or use the included ./gradlew wrapper)

Raspberry Pi -which one doesn't matter 

# Local Setup:
Clone the Repository
```
git clone https://github.com/yourusername/Raspberry-PI-Server.git
cd Raspberry-PI-Server
```
Open MySQL terminal and run:
````
CREATE DATABASE IF NOT EXISTS cloud_storage;
USE cloud_storage;

CREATE TABLE IF NOT EXISTS file_metadata (
    id INT AUTO_INCREMENT PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    path VARCHAR(500) NOT NULL,
    size BIGINT NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uploaded_by VARCHAR(255),
    ip_address VARCHAR(45),
    category VARCHAR(50) NOT NULL
);
````
Create application-local.properties, which is gitignored and contains your personal credentials
````
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/cloud_storage
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

# App login credentials
app.admin.username=YOUR_USERNAME
app.admin.password=YOUR_PASSWORD

# Storage path — where uploaded files will be saved on your machine
storage.location=/your/path/to/server_files
````
# Run the Application:

1.In Intellij:
-Open Edit Configurations and 
Set Active profiles to local

-send the Project as a -jar file your Raspberry Pi

-make your Raspberry Pi headless and use it per SSH and run the jar File

-Open the browser http://localhost:8088 and login with your credentials from the application-local.properties

# Notes:
The storage path must exist or the app will create it automatically on startup
File metadata is stored in MySQL but the actual files are stored on the filesystem

## Roadmap:

### In Progress:
- more Unit & integration tests with the help of Mockito 
- Docker containerization + docker-compose

### Planned:
- Database-backed user management (replace InMemory auth)
- Search files by username or date
- Modern UI with Bootstrap/Tailwind
- Mobile-optimized interface
- File versioning
- Delete files from the UI
- CI/CD pipeline with GitHub Actions


