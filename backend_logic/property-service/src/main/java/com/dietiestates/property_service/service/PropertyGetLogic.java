// src/main/java/com/dietiestates/property_service/service/PropertyGetLogic.java
package com.dietiestates.property_service.service;

import com.dietiestates.property_service.dto.PropertyDetailDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@Service
public class PropertyGetLogic {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyGetLogic(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public PropertyDetailDTO getById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Property not found with id " + id));

        return new PropertyDetailDTO(
                property.getId(),
                property.getTitle(),
                property.getCity(),
                property.getArea(),
                property.getPrice(),
                property.getDescription(), // se esiste
                property.getpublishedAt() != null
                        ? property.getpublishedAt().format(DateTimeFormatter.ISO_DATE)
                        : null
        );
    }
}
