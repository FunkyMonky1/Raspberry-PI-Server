package com.cloudserver.pi.uploadingfiles;

import com.cloudserver.pi.model.FileCategory;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();

    void store(MultipartFile file, String username, String ipAddress, FileCategory category);
    
    Resource loadAsResource(String filename, FileCategory category);
    
}
