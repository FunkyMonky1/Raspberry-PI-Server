CREATE DATABASE IF NOT EXISTS cloud_storage;
       
USE cloud_storage;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE file_metadata (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               original_filename VARCHAR(255) NOT NULL,
                               stored_filename VARCHAR(255) NOT NULL,
                               path VARCHAR(500) NOT NULL,
                               size BIGINT NOT NULL,
                               content_type VARCHAR(100),
                               upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               user_id INT,
                               CONSTRAINT fk_file_user FOREIGN KEY (user_id) REFERENCES users(id)
);

