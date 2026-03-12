package com.backend.controller;

import com.backend.service.SearchService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

//import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import com.backend.dto.PropertyCreateDTO;
import com.backend.dto.PropertyDetailDTO;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    @org.springframework.beans.factory.annotation.Autowired
    private SearchService searchService;

    @GetMapping("")
    public ResponseEntity<List<PropertyDetailDTO>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String listingType, // vendita / affitto
            @RequestParam(required = false) Integer rooms, // numero stanze
            @RequestParam(required = false) String energyClass // classe energetica
    ) {
        System.out.println("Cerco in PropertyController ");

        List<PropertyDetailDTO> results = searchService.searchProperties(
                city, minArea, maxPrice, listingType, rooms, energyClass);

        return ResponseEntity.ok(results);

    }

@GetMapping("/bybounds")
public ResponseEntity<List<PropertyDetailDTO>> searchByBounds(
        @RequestParam Double lat,
        @RequestParam Double lon,
        @RequestParam Double radiusKm) {

    if (lat < -90 || lat > 90)
        return ResponseEntity.badRequest().build();

    if (lon < -180 || lon > 180)
        return ResponseEntity.badRequest().build();

    if (radiusKm < 0 || radiusKm > 40075)
        return ResponseEntity.badRequest().build();

    List<PropertyDetailDTO> results =
            searchService.searchByBounds(lat, lon, radiusKm);

    return ResponseEntity.ok(results);
}

}
