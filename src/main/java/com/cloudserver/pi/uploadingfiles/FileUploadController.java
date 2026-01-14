package com.cloudserver.pi.uploadingfiles;

import com.cloudserver.pi.model.FileCategory;
import com.cloudserver.pi.model.FileMetadata;
import com.cloudserver.pi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class FileUploadController {

    private final StorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;

    @Autowired
    public FileUploadController(StorageService storageService,
                                FileMetadataRepository fileMetadataRepository) {
        this.storageService = storageService;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    /**
     * Show files grouped by category. Optional filter by category.
     */
    @GetMapping("/")
    public String listUploadedFiles(Model model,
                                    @RequestParam(required = false) String category) {

        List<FileMetadata> files;

        if (category != null && !category.isEmpty()) {
            // Convert String to enum safely
            FileCategory catEnum;
            try {
                catEnum = FileCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                catEnum = null; // invalid category
            }

            if (catEnum != null) {
                files = fileMetadataRepository.findByCategory(catEnum);
            } else {
                files = fileMetadataRepository.findAll();
            }
        } else {
            files = fileMetadataRepository.findAll();
        }

        model.addAttribute("files", files);
        model.addAttribute("categories", FileCategory.values()); // for template dropdown
        return "uploadForm"; // Thymeleaf template
    }

    /**
     * Upload a file with category and store metadata
     */
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @AuthenticationPrincipal User currentUser,
                                   @RequestParam("category") String category,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {

        // Convert String to FileCategory enum
        FileCategory catEnum;
        try {
            catEnum = FileCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "Invalid category selected!");
            return "redirect:/";
        }

        // Get user IP
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isBlank()) {
            ipAddress = request.getRemoteAddr();
        }

        // Store file
        storageService.store(file, currentUser, ipAddress, catEnum);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";
    }

    /**
     * Download a file by its ID
     */
    @GetMapping("/files/download/{id}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable Long id) {
        FileMetadata fileMeta = fileMetadataRepository.findById(id)
                .orElseThrow(() -> new StorageFileNotFoundException("File not found"));

        Resource file = storageService.loadAsResource(fileMeta.getStoredFilename(), fileMeta.getCategory());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileMeta.getOriginalFilename() + "\"")
                .body(file);
    }

    /**
     * Handle file-not-found exceptions
     */
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}

