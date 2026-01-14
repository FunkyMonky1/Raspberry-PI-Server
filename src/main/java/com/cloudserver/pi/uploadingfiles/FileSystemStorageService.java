package com.cloudserver.pi.uploadingfiles;

import com.cloudserver.pi.model.FileCategory;
import com.cloudserver.pi.model.FileMetadata;
import com.cloudserver.pi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("File upload location cannot be empty.");
        }
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file, User currentUser, String ipAddress, FileCategory category) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            // Ensure category folder exists
            Path categoryPath = rootLocation.resolve(category.name());
            Files.createDirectories(categoryPath);

            // Generate a unique filename before saving
            String storedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destinationFile = categoryPath.resolve(storedFilename)
                    .normalize().toAbsolutePath();

            // Security check: file must be inside root folder
            if (!destinationFile.startsWith(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside storage directory.");
            }

            // Copy file to destination
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Save metadata
            FileMetadata metadata = new FileMetadata();
            metadata.setOriginalFilename(file.getOriginalFilename());
            metadata.setStoredFilename(storedFilename);
            metadata.setPath(destinationFile.toString());
            metadata.setCategory(category);
            metadata.setSize(file.getSize());
            metadata.setUser(currentUser);
            metadata.setUploadDate(LocalDateTime.now());
            metadata.setIpAddress(ipAddress);

            fileMetadataRepository.save(metadata);

        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            // Include subfolders (categories)
            return Files.walk(rootLocation, 2)
                    .filter(path -> !path.equals(rootLocation))
                    .map(rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String storedFilename, FileCategory category) {
        try {
            Path file = rootLocation.resolve(category.name()).resolve(storedFilename)
                    .normalize().toAbsolutePath();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + storedFilename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + storedFilename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
