package com.dietiestates.property_service.service;
import com.dietiestates.property_service.dto.PropertyCreateDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class PropertyCreateLogic {

    private final PropertyRepository propertyRepository;

    public PropertyCreateLogic(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // Metodo per creare una nuova proprietà
    public PropertyCreateDTO createProperty(PropertyCreateDTO createDTO) {
        // Creazione oggetto Property a partire dal DTO
        Property property = new Property();
        property.setTitle(createDTO.getTitle());
        property.setCity(createDTO.getCity());
        property.setArea(createDTO.getArea());
        property.setPrice(createDTO.getPrice());
        property.setpublishedAt(java.time.LocalDate.now()); // Imposta la data di pubblicazione

        // Salvataggio nel database
        Property savedProperty = propertyRepository.save(property);

        // Conversione in DTO da restituire
        return new PropertyCreateDTO(
            savedProperty.getTitle(),
            savedProperty.getCity(),
            savedProperty.getArea(),
            savedProperty.getPrice(),
            savedProperty.getpublishedAt()
        );
    }
}
