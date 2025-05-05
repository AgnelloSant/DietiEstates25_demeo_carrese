package com.dietiestates.property_service.service;
import com.dietiestates.property_service.dto.PropertyUpdateDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PropertyUpdateLogic {

    private final PropertyRepository propertyRepository;

    public PropertyUpdateLogic(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Metodo per aggiornare una proprietà esistente
    public PropertyUpdateDTO updateProperty(Long id, PropertyUpdateDTO updateDTO) {
        // Ricerca della proprietà esistente
        Optional<Property> existingPropertyOpt = propertyRepository.findById(id);

        if (existingPropertyOpt.isEmpty()) {
            throw new RuntimeException("Property not found with id: " + id);
        }

        Property property = existingPropertyOpt.get();

        // Aggiornamento dei campi
        property.setTitle(updateDTO.getTitle());
        property.setCity(updateDTO.getCity());
        property.setArea(updateDTO.getArea());
        property.setPrice(updateDTO.getPrice());

        // Salvataggio aggiornato
        Property updatedProperty = propertyRepository.save(property);

        // Conversione in DTO da restituire
        return new PropertyUpdateDTO(
            updatedProperty.getTitle(),
            updatedProperty.getCity(),
            updatedProperty.getArea(),
            updatedProperty.getPrice()
        );
    }
}
