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

    public PropertyUpdateDTO updateProperty(Long id, PropertyUpdateDTO updateDTO) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        // Aggiorna campi base
        property.setTitle(updateDTO.getTitle());
        property.setCity(updateDTO.getCity());
        property.setArea(updateDTO.getArea());
        property.setPrice(updateDTO.getPrice());

        //  campi ricerca avanzata 
        property.setListingType(updateDTO.getListingType());
        property.setRooms(updateDTO.getRooms());
        property.setEnergyClass(updateDTO.getEnergyClass());

        Property updatedProperty = propertyRepository.save(property);

        return new PropertyUpdateDTO(
            updatedProperty.getTitle(),
            updatedProperty.getCity(),
            updatedProperty.getArea(),
            updatedProperty.getPrice(),
            updatedProperty.getListingType(),
            updatedProperty.getRooms(),
            updatedProperty.getEnergyClass(),
            updatedProperty.getAddress()
        );
    }
}
