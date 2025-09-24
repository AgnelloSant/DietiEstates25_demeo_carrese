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

    // Recupera una proprietà singola
    public PropertyDetailDTO getById(Long id) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Property not found with id " + id));

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
            property.isNearTransport(),
            property.getListingType(),
            property.getRooms(),
            property.getEnergyClass(),
            property.getAddress(),
            property.getIdUser(),
            property.getViews()
        );
    }

    // Recupera più proprietà (es. preferiti)
    public List<PropertySearchDTO> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        List<Property> props = propertyRepository.findAllById(ids);

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
                p.isNearTransport(),
                p.getListingType(),
                p.getRooms(),
                p.getEnergyClass(),
                p.getAddress(),
                p.getLatitude(),
                p.getLongitude()
            ));
        }

        // Ordine input preservato
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);
        dtos.sort(Comparator.comparingInt(d -> order.getOrDefault(d.getId(), Integer.MAX_VALUE)));

        return dtos;
    }
}
