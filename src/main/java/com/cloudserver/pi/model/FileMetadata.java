package com.cloudserver.pi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "file_metadata") 
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "stored_filename")
    private String storedFilename;

    private String path;

    private Long size;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    // Verknüpfung zum User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // es darf nicht leer sein 
    private FileCategory category;
    // i want to make categories math etc.

    
    public FileMetadata() {
        this.uploadDate = LocalDateTime.now();
    }


}

    