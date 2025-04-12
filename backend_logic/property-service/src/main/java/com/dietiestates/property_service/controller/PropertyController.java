package com.dietiestates.property_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dietiestates.property_service.dto.PropertyDTO;
import com.dietiestates.property_service.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // Endpoint di ricerca
    @GetMapping("/search")
    public ResponseEntity<List<PropertyDTO>> searchProperties(
        @RequestParam String city,
        @RequestParam Double minArea,
        @RequestParam Double maxPrice
    ) {
        List<PropertyDTO> results = propertyService.searchProperties(city, minArea, maxPrice);
        return ResponseEntity.ok(results);
    }
}