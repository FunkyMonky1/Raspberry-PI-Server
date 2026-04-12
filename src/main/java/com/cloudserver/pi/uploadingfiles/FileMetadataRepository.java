package com.cloudserver.pi.uploadingfiles;

import com.cloudserver.pi.model.FileCategory;
import com.cloudserver.pi.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    
    List<FileMetadata> findByCategory(FileCategory category);
}
