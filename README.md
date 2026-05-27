# Raspberry-PI-Server
A self-hosted personal cloud server built with Java Spring Boot, running on a Raspberry Pi.
Upload, organize, and download university materials by category — secured behind authentication.

## Requirements
- **Java 17** — older Raspberry Pis only support this version
- **MySQL** — stores file metadata
- **Gradle** — build tool (or use the included `./gradlew` wrapper)
- **Raspberry Pi** — any model

---

## Local Setup (without Docker)

### 1. Clone the repository
```bash
git clone https://github.com/FunkyMonky1/Raspberry-PI-Server.git
cd Raspberry-PI-Server
```

### 2. Set up the MySQL database
Open a MySQL terminal and run:
```sql
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
```

### 3. Create application-local.properties
This file is gitignored — create it at `src/main/resources/application-local.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cloud_storage?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

app.admin.username=YOUR_USERNAME
app.admin.password=YOUR_PASSWORD

storage.location=./server_files
```

### 4. Run the application

**IntelliJ:**
Run → Edit Configurations → Active profiles → set to `local`

**Terminal:**
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 5. Open in browser
```
http://localhost:8088
```
Log in with the credentials from your `application-local.properties`.

---

## Docker Setup (local)

### 1. Create your .env file
Copy the example and fill in your values:
```bash
cp .env.example .env
```

### 2. Start with Docker Compose
```bash
docker-compose up --build
```

### 3. Open in browser
```
http://localhost:8088
```

### Check uploaded files inside the container
```bash
docker exec -it cloud-server-app sh
ls -la /app/server_files
```

---

## Deploying to Raspberry Pi (with Docker)

### 1. Install Docker on the Pi
```bash
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
```

### 2. Clone the repo on the Pi
```bash
cd ~
git clone https://github.com/FunkyMonky1/Raspberry-PI-Server.git
cd Raspberry-PI-Server
```

### 3. Create .env and start
```bash
cp .env.example .env
# Edit .env with your credentials
docker-compose up --build
```

### 4. Access from any device on the network
Find the Pi's IP:
```bash
hostname -I
```
Then open in any browser on the same WiFi:
```
http://PI_IP:8088
```

---

## Note for developers using Java 21+

If you have a newer JDK (e.g. Java 25) as your default but Java 17 installed alongside it, `gradle.properties` already configures the Gradle daemon to use Java 17 automatically. No manual setup needed.

---

## Notes
- Uploaded files are stored on the filesystem; only metadata goes into MySQL.
- The storage path is created automatically on startup if it does not exist.

---

## Roadmap

### Done:
- File upload with category organization
- File type validation (blocks dangerous extensions)
- IP address logging per upload
- Spring Security login
- Docker containerization with docker-compose

### In Progress:
- More unit & integration tests with Mockito

### Planned:
- Database-backed user management (replace in-memory auth)
- Search files by username or date
- Delete files from the UI
- Modern UI with Bootstrap or Tailwind
- Mobile-optimized interface
- File versioning
- CI/CD pipeline with GitHub Actions
