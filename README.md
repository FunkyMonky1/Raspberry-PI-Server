# Raspberry-PI-Server
Making my own Self-Hosted Cloud-Server where i upload files and pictures for Studies and have them organized by categories.
With this little Project i wanted to dip my toes into Java Spring Boot which i will learn in University.

#Requirements:
Java 17 - older Raspberry Pi's only support this version
MySQL -database for storing file metadata
Gradle -build tool (or use the included ./gradlew wrapper)
Raspberry Pi 

#Local Setup:
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
#Run the Application:

1.In Intellij:
-Open Edit Configurations
Set Active profiles to local

-send the Project as a -jar file your Raspberry Pi
-make your Raspberry Pi headless and use it per SSH and run the jar File
-Open the browser http://localhost:8088 and login with your credentials from the application-local.properties

#Notes:
The storage path must exist or the app will create it automatically on startup
File metadata is stored in MySQL but the actual files are stored on the filesystem




