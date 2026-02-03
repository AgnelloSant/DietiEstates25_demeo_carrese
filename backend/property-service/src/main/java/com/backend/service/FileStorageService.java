package com.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.List;
import java.util.ArrayList;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("/app/uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public void saveImage(MultipartFile file, Long propertyId) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".jpg")) {
                throw new RuntimeException("Only .jpg files are allowed.");
            }

            Path propertyDir = rootLocation.resolve("property_" + propertyId);
            if (!Files.exists(propertyDir)) {
                Files.createDirectories(propertyDir);
            }

            filename = Paths.get(filename).getFileName().toString();

            Path destinationFile = propertyDir.resolve(filename);

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public List<String> getImages(Long propertyId) {
        Path propertyDir = rootLocation.resolve("property_" + propertyId);
        if (!Files.exists(propertyDir)) {
            return new ArrayList<>();
        }

        try (Stream<Path> stream = Files.list(propertyDir)) {
            // Return relative paths like "property_123/image.jpg"
            // accessible via static handler mapping
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(file -> "property_" + propertyId + "/" + file.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }
}
