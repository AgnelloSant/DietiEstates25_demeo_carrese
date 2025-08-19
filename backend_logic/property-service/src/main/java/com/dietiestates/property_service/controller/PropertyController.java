package com.dietiestates.property_service.controller;

import com.dietiestates.property_service.dto.*;
import com.dietiestates.property_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertySearchLogic propertySearchLogic;
    private final PropertyCreateLogic propertyCreateLogic;
    private final PropertyUpdateLogic propertyUpdateLogic;
    private final PropertyDeleteLogic propertyDeleteLogic;

    @Autowired
    public PropertyController(
        PropertySearchLogic propertySearchLogic,
        PropertyCreateLogic propertyCreateLogic,
        PropertyUpdateLogic propertyUpdateLogic,
        PropertyDeleteLogic propertyDeleteLogic
    ) {
        this.propertySearchLogic = propertySearchLogic;
        this.propertyCreateLogic = propertyCreateLogic;
        this.propertyUpdateLogic = propertyUpdateLogic;
        this.propertyDeleteLogic = propertyDeleteLogic;
    }

    // --- Ricerca proprietà ---
    @GetMapping("/search")
    public ResponseEntity<List<PropertySearchDTO>> searchProperties(
        @RequestParam String city,
        @RequestParam Double minArea,
        @RequestParam Double maxPrice
    ) {
        List<PropertySearchDTO> results = propertySearchLogic.searchProperties(city, minArea, maxPrice);
        return ResponseEntity.ok(results);
    }

    // --- Creazione proprietà ---
    @PostMapping("/create")
    public ResponseEntity<PropertyCreateDTO> createProperty(@RequestBody PropertyCreateDTO createDTO) {
        PropertyCreateDTO created = propertyCreateLogic.createProperty(createDTO);
        return ResponseEntity.ok(created);
    }

    // --- Aggiornamento proprietà ---
    @PutMapping("/update/{id}")
    public ResponseEntity<PropertyUpdateDTO> updateProperty(
        @PathVariable Long id,
        @RequestBody PropertyUpdateDTO updateDTO
    ) {
        PropertyUpdateDTO updated = propertyUpdateLogic.updateProperty(id, updateDTO);
        return ResponseEntity.ok(updated);
    }

    // --- Cancellazione proprietà ---
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        propertyDeleteLogic.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully.");
    }
}
