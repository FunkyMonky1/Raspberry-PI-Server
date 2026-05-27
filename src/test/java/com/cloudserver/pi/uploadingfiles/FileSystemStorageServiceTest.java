package com.cloudserver.pi.uploadingfiles;

import com.cloudserver.pi.model.FileCategory;
import com.cloudserver.pi.model.FileMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class FileSystemStorageServiceTest {

    // @Mock creates a fake FileMetadataRepository — no real DB needed
    @Mock
    private FileMetadataRepository fileMetadataRepository;

    // JUnit creates a real temporary folder and deletes it after the test
    @TempDir
    Path tempDir;

    private FileSystemStorageService storageService;

    @BeforeEach
    void setUp() {
        // Build a real StorageProperties pointing at the temp folder
        StorageProperties props = new StorageProperties();
        props.setLocation(tempDir.toString());

        // Manually construct the service — inject both the real props and the mock repo
        storageService = new FileSystemStorageService(props, fileMetadataRepository);
        storageService.init(); // creates the root directory
    }

    // -------------------------------------------------------------------------
    // Example 1: verify() — check that the right data was saved to the DB
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Storing a valid file saves correct metadata to the repository")
    void store_validFile_savesMetadata() {
        // Arrange: a fake uploaded file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lecture.pdf",
                "application/pdf",
                "some pdf content".getBytes()
        );

        // Act
        storageService.store(file, "felix", "127.0.0.1", FileCategory.MATH);

        // Assert: capture the FileMetadata object that was passed to save()
        // ArgumentCaptor lets you inspect the exact argument Mockito received
        ArgumentCaptor<FileMetadata> captor = ArgumentCaptor.forClass(FileMetadata.class);
        verify(fileMetadataRepository).save(captor.capture());

        FileMetadata saved = captor.getValue();
        assertThat(saved.getOriginalFilename()).isEqualTo("lecture.pdf");
        assertThat(saved.getUploadedBy()).isEqualTo("felix");
        assertThat(saved.getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(saved.getCategory()).isEqualTo(FileCategory.MATH);
        assertThat(saved.getSize()).isGreaterThan(0);
    }

    // -------------------------------------------------------------------------
    // Example 2: assertThrows() — check that bad input causes the right error
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("Storing an empty file throws StorageException")
    void store_emptyFile_throwsStorageException() {
        // Arrange: a file with no content
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]   // <-- zero bytes
        );

        // Act + Assert: the lambda runs the code; assertThrows verifies the exception type
        assertThrows(StorageException.class, () ->
                storageService.store(emptyFile, "felix", "127.0.0.1", FileCategory.MATH)
        );
    }
    @Test
    @DisplayName("loadAsResource with unknown filename throws StorageFileNotFoundException")
    void loadAsResource_unknownFilename_throwsStorageFileNotFoundException() {
        assertThrows(StorageFileNotFoundException.class, () ->
                storageService.loadAsResource("does_not_exist.pdf", FileCategory.MATH)
        );
    }
    @Test
    @DisplayName("Store a file with a malicious filename throws StorageException")
    void store_malicious_file_throwsStorageException() {
        //Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "../../../../etc/passwd",
                "passwords",
                "secretetet".getBytes()   // so it is not empty
        );
        //Act + Assert
        StorageException ex = assertThrows(StorageException.class, () ->
                storageService.store(emptyFile,"fdeded", "127.0.0.1", FileCategory.MATH)
        );
        assertThat(ex.getMessage()).isEqualTo("Cannot store file outside storage directory.");
    }
    
    
}
