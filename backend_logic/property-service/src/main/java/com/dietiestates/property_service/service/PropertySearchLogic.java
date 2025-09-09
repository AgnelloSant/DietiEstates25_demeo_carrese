package com.dietiestates.property_service.service;

import com.dietiestates.shared.dto.PropertySearchDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertySearchLogic {

    private final PropertyRepository propertyRepository;

    public PropertySearchLogic(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    
      //Ricerca proprietà filtrata per città, area minima e prezzo massimo.
    // Restituisce una lista di PropertySearchDTO (con id incluso).
     
public List<PropertySearchDTO> searchProperties(String city, Double minArea, Double maxPrice) {
    System.err.println("PropertySearchLogic.searchProperties: city=" + city + " minArea=" + minArea + " maxPrice=" + maxPrice);
    List<Property> properties = propertyRepository
        .searchProperties(city, minArea, maxPrice); // 👈 usa il nuovo metodo

    return properties.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
}
   
    private PropertySearchDTO convertToDto(Property property) {
        return new PropertySearchDTO(
            property.getId(),      // 👈 adesso includiamo l'id
            property.getTitle(),
            property.getCity(),
            property.getArea(),
            property.getPrice()
        );
    }


}
