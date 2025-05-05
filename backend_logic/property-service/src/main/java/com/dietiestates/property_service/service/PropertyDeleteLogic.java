package com.dietiestates.property_service.service;

import com.dietiestates.property_service.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class PropertyDeleteLogic {

    private final PropertyRepository propertyRepository;

    public PropertyDeleteLogic(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Metodo per cancellare una proprietà
    public void deleteProperty(Long id) {
        // Verifica se la proprietà esiste
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found with id: " + id);
        }

        // Eliminazione della proprietà
        propertyRepository.deleteById(id);
    }
}
