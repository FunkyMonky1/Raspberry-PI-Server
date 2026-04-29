# Raspberry-PI-Server
A self-hosted personal cloud server built with Java Spring Boot, running on a Raspberry Pi. 
Upload, organize, and download university materials by category — secured behind authentication.

# Requirements:
Java 17 -older Raspberry Pi's only support this version 

MySQL -database for storing file metadata

Gradle -build tool (or use the included ./gradlew wrapper)

Raspberry Pi -which one doesn't matter 

Docker and Docker Compose installed

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

-create a .env file in the project root with your own credentials:
```
DB_NAME=cloud_storage
DB_USER=name
DB_PASSWORD=your_mysql_password
ADMIN_USER=felix
ADMIN_PASSWORD=your_app_password
```
-Build and run the application:
```
docker-compose up --build
http://localhost:8088
```
-Check where the file is uploaded
```
docker exec -it cloud-server-app sh
ls -la /app/server_files
```
## Roadmap:

### In Progress:
- more Unit & integration tests with the help of Mockito 

### Planned:
- Database-backed user management (replace InMemory auth)
- Search files by username or date
- Modern UI with Bootstrap/Tailwind
- Mobile-optimized interface
- File versioning
- Delete files from the UI
- CI/CD pipeline with GitHub Actions


