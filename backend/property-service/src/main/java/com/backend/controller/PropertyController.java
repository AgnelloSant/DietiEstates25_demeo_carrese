package com.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import com.backend.dto.PropertyCreateDTO;
import com.backend.dto.PropertyDetailDTO;
import com.backend.dto.PropertyUpdateDTO;
import com.backend.dto.IdsRequest;
import com.backend.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/create")
    public ResponseEntity<PropertyCreateDTO> createProperty(@RequestBody PropertyCreateDTO createDTO) {
        return ResponseEntity.ok(propertyService.createProperty(createDTO));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<PropertyDetailDTO>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDetailDTO> getProperty(@PathVariable Long id) {
        System.out.println("DEBUG PropertyController: getProperty called for id: " + id);
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PropertyUpdateDTO> updateProperty(@PathVariable Long id,
            @RequestBody PropertyUpdateDTO updateDTO) {
        return ResponseEntity.ok(propertyService.updateProperty(id, updateDTO));
    }

    @PostMapping("/updateviews/{id}")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        propertyService.incrementPropertyViews(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getByIds")
    public ResponseEntity<List<PropertyDetailDTO>> getPropertiesByIds(@RequestBody IdsRequest idsRequest) {
        return ResponseEntity.ok(propertyService.findByIds(idsRequest.getIds()));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        propertyService.uploadImage(file, id);
        return ResponseEntity.ok("Image uploaded successfully");
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<String>> getPropertyImages(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyImages(id));
    }
}
