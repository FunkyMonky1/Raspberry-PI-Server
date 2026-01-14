package com.cloudserver.pi.uploadingfiles;

import com.cloudserver.pi.model.FileCategory;
import com.cloudserver.pi.model.FileMetadata;
import com.cloudserver.pi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    // Optional: alle Dateien eines Users abrufen
    List<FileMetadata> findAllByUser(User user);
    List<FileMetadata> findByCategory(FileCategory category);
}
