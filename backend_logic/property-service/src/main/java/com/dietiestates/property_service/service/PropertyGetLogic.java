// src/main/java/com/dietiestates/property_service/service/PropertyGetLogic.java
package com.dietiestates.property_service.service;

import com.dietiestates.property_service.dto.PropertyDetailDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
import com.dietiestates.shared.dto.PropertySearchDTO; // ✅ import dal JAR shared

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PropertyGetLogic {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyGetLogic(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // 🔹 Recupera una proprietà per ID e la converte in PropertyDetailDTO
    public PropertyDetailDTO getById(Long id) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Property not found with id " + id));

        // ✅ Converte in DTO includendo i nuovi campi
        return new PropertyDetailDTO(
            property.getId(),
            property.getTitle(),
            property.getCity(),
            property.getArea(),
            property.getPrice(),
            property.getDescription(),
            property.getPublishedAt() != null
                ? property.getPublishedAt().format(DateTimeFormatter.ISO_DATE)
                : null,
            property.getLatitude(),
            property.getLongitude(),
            property.isNearSchool(),
            property.isNearPark(),
            property.isNearTransport()
        );
    }

    // 🔹 Recupera più proprietà per lista di ID (usato nei preferiti)
    public List<PropertySearchDTO> findByIds(List<Long> ids) {
        System.out.println("PropertyGetLogic.findByIds: ");
        if (ids == null || ids.isEmpty()) return List.of();

        // 1) Fetch dal DB
        List<Property> props = propertyRepository.findAllById(ids);

        // 2) Conversione in DTO includendo i flag nearX
        List<PropertySearchDTO> dtos = new ArrayList<>(props.size());
        for (Property p : props) {
            dtos.add(new PropertySearchDTO(
                p.getId(),
                p.getTitle(),
                p.getCity(),
                p.getArea(),
                p.getPrice(),
                p.isNearSchool(),
                p.isNearPark(),
                p.isNearTransport()
            ));
        }

        // 3) Preserva l’ordine degli ID passati in input
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);
        dtos.sort(Comparator.comparingInt(d -> order.getOrDefault(d.getId(), Integer.MAX_VALUE)));

        return dtos;
    }
}
