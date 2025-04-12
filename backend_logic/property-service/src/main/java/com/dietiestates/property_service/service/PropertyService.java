package com.dietiestates.property_service.service;
import com.dietiestates.property_service.dto.PropertyDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Metodo per la ricerca filtrata
    public List<PropertyDTO> searchProperties(String city, Double minArea, Double maxPrice) {
        // Chiamata al Repository con i criteri di ricerca
        List<Property> properties = propertyRepository
        .findByCityAndAreaGreaterThanEqualAndPriceLessThanEqualOrderByPublishedAtDesc(city, minArea, maxPrice);

        // Conversione da Property (entità) a PropertyDTO
        return properties.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // Metodo di supporto per la conversione
    private PropertyDTO convertToDto(Property property) {
        return new PropertyDTO(
            property.getTitle(),
            property.getCity(),
            property.getArea(),
            property.getPrice()
        );
    }
}