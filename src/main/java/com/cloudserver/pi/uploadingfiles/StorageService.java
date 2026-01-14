package com.cloudserver.pi.uploadingfiles;

import com.cloudserver.pi.model.FileCategory;
import com.cloudserver.pi.model.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(MultipartFile file, User currentUser, String ipAddress, FileCategory category);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename, FileCategory category);

    void deleteAll();
}
