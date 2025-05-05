package com.dietiestates.property_service.service;
import com.dietiestates.property_service.dto.PropertySearchDTO;
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

    // Metodo per la ricerca filtrata
    public List<PropertySearchDTO> searchProperties(String city, Double minArea, Double maxPrice) {
        // Chiamata al Repository con i criteri di ricerca
        List<Property> properties = propertyRepository
        .findByCityAndAreaGreaterThanEqualAndPriceLessThanEqualOrderByPublishedAtDesc(city, minArea, maxPrice);

        // Conversione da Property (entità) a PropertySearchDTO
        return properties.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // Metodo di supporto per la conversione
    private PropertySearchDTO convertToDto(Property property) {
        return new PropertySearchDTO(
            property.getTitle(),
            property.getCity(),
            property.getArea(),
            property.getPrice()
        );
    }
}