package com.dietiestates.property_service.controller;

import com.dietiestates.property_service.dto.*;
import com.dietiestates.property_service.service.*;
import com.dietiestates.shared.dto.PropertySearchDTO;
import com.dietiestates.shared.dto.IdsRequest;
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
    private final PropertyGetLogic propertyGetLogic; 

    @Autowired
    public PropertyController(
        PropertySearchLogic propertySearchLogic,
        PropertyCreateLogic propertyCreateLogic,
        PropertyUpdateLogic propertyUpdateLogic,
        PropertyDeleteLogic propertyDeleteLogic,
        PropertyGetLogic propertyGetLogic
    ) {
        this.propertySearchLogic = propertySearchLogic;
        this.propertyCreateLogic = propertyCreateLogic;
        this.propertyUpdateLogic = propertyUpdateLogic;
        this.propertyDeleteLogic = propertyDeleteLogic;
        this.propertyGetLogic= propertyGetLogic;
    }

    // --- Ricerca proprietà ---
  @GetMapping("/search")
public ResponseEntity<List<PropertySearchDTO>> searchProperties(
    @RequestParam(required = false) String city,
    @RequestParam(required = false) Double minArea,
    @RequestParam(required = false) Double maxPrice
) {
    System.out.println("Cerco in propertyController: city=" + city + " minArea=" + minArea + " maxPrice=" + maxPrice);
    List<PropertySearchDTO> results = propertySearchLogic.searchProperties(city, minArea, maxPrice);
    return ResponseEntity.ok(results);
}


    // --- Creazione proprietà ---
    @PostMapping("/create")
public ResponseEntity<PropertyCreateDTO> createProperty(@RequestBody PropertyCreateDTO createDTO) {
    PropertyCreateDTO created = propertyCreateLogic.createProperty(createDTO); // ✅ ora viene usato
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

    @PostMapping("/batch")
    public ResponseEntity<List<PropertySearchDTO>> getByIds(@RequestBody IdsRequest req){
        System.out.println("PropertyController.getByIds");
        var out = propertyGetLogic.findByIds(req.getIds());
        return ResponseEntity.ok(out);
    }



    // --- Cancellazione proprietà ---
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        propertyDeleteLogic.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully.");
    }

@GetMapping("/{id}")
public ResponseEntity<PropertyDetailDTO> getProperty(@PathVariable Long id) {
    return ResponseEntity.ok(propertyGetLogic.getById(id)); 
}


}
