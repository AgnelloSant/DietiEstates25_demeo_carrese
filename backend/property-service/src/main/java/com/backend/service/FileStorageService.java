package com.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Comparator;

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
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("Invalid file name.");
        }

        String cleanFilename = Paths.get(originalFilename).getFileName().toString();
        String lowerName = cleanFilename.toLowerCase();

        if (!lowerName.endsWith(".jpg") && !lowerName.endsWith(".jpeg")) {
            throw new RuntimeException("Only .jpg and .jpeg files are allowed.");
        }

        Path propertyDir = rootLocation.resolve("property_" + propertyId);
        if (!Files.exists(propertyDir)) {
            Files.createDirectories(propertyDir);
        }

        String extension = lowerName.endsWith(".jpeg") ? ".jpeg" : ".jpg";
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path destinationFile = propertyDir.resolve(uniqueFilename);

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

public void deletePropertyImages(Long propertyId) {
    Path propertyDir = rootLocation.resolve("property_" + propertyId);

    if (!Files.exists(propertyDir)) {
        return;
    }

    try (Stream<Path> walk = Files.walk(propertyDir)) {
        walk.sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete file: " + path, e);
                }
            });
    } catch (IOException e) {
        throw new RuntimeException("Failed to delete property image directory", e);
    }
}





}
