package com.thatfunnyfeeling.art_gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/artworks")
@CrossOrigin(origins = "http://localhost:3000")
public class ArtworkController {
    private static final Logger logger = LoggerFactory.getLogger(ArtworkController.class);
    private final List<Map<String, String>> artworks = new ArrayList<>();
    private final String uploadDir;

    public ArtworkController() {
        // Create uploads directory in project root
        uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            logger.info("Upload directory created at {}: {}", uploadDir, created);
        }
    }

    @GetMapping("/ping")
    public String ping() {
        logger.info("Ping endpoint accessed");
        return "pong";
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        logger.info("Test endpoint accessed");
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Backend is working!");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getAllArtworks() {
        logger.info("Getting all artworks. Total count: {}", artworks.size());
        return ResponseEntity.ok(artworks);
    }

        @PostMapping
    public ResponseEntity<Map<String, Object>> uploadArtwork(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist) {
        logger.info("Upload request received - Title: {}, Artist: {}", title, artist);

        if (file.isEmpty() || title.isEmpty() || artist.isEmpty()) {
            logger.warn("Upload request missing required fields");
            Map<String, Object> response = new HashMap<>();
            response.put("message", "All fields are required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + filename);
            logger.info("Attempting to save file to: {}", destination.getAbsolutePath());
            file.transferTo(destination);
            logger.info("File saved successfully: {}", filename);

            Map<String, String> artwork = new HashMap<>();
            artwork.put("title", title);
            artwork.put("artist", artist);
            artwork.put("url", "/uploads/" + filename);
            artworks.add(artwork);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Artwork uploaded successfully!");
            response.put("artworkId", artworks.size() - 1);
            response.put("imageUrl", artwork.get("url"));
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("Error uploading file", e);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Error uploading artwork: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}