package com.cloudserver.pi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;       // Dateiname
    private String filepath;       // Pfad auf Platte
    private Long size;             // Dateigröße
    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;            // Wer die Datei hochgeladen hat

    // Getter & Setter...
}

