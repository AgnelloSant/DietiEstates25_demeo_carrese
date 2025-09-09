
package com.dietiestates.property_service.service;

import com.dietiestates.property_service.dto.PropertyCreateDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class PropertyCreateLogic {

    private final PropertyRepository propertyRepository;
    private final GeoapifyService geoapifyService; // servizio esterno per controllare i POI (schools, parks, transport)

    public PropertyCreateLogic(PropertyRepository propertyRepository, GeoapifyService geoapifyService) {
        this.propertyRepository = propertyRepository;
        this.geoapifyService = geoapifyService;
    }

    /**
     * Crea una nuova proprietà nel DB:
     * - copia i dati dal DTO
     * - imposta la data di pubblicazione
     * - chiama Geoapify per verificare scuole, parchi, trasporti nelle vicinanze
     * - salva tutto nel DB
     */
    public PropertyCreateDTO createProperty(PropertyCreateDTO createDTO) {
        // 1️⃣ Crea l'entità a partire dal DTO
        Property property = new Property();
        property.setTitle(createDTO.getTitle());
        property.setCity(createDTO.getCity());
        property.setArea(createDTO.getArea());
        property.setPrice(createDTO.getPrice());
        property.setPublishedAt(LocalDate.now());
        property.setLatitude(createDTO.getLatitude());   // coordinate
        property.setLongitude(createDTO.getLongitude());

        // 2️⃣ Chiama l’API Geoapify per i servizi vicini
        Map<String, Boolean> features = geoapifyService.checkNearbyPlaces(
            createDTO.getLatitude(),
            createDTO.getLongitude()
        );

        // 3️⃣ Salva i risultati nell’entità
        property.setNearSchool(features.get("nearSchool"));
        property.setNearPark(features.get("nearPark"));
        property.setNearTransport(features.get("nearTransport"));

        // 4️⃣ Salvataggio nel database
        Property savedProperty = propertyRepository.save(property);

        // 5️⃣ Conversione in DTO da restituire
        return new PropertyCreateDTO(
            savedProperty.getTitle(),
            savedProperty.getCity(),
            savedProperty.getArea(),
            savedProperty.getPrice(),
            null,
             savedProperty.getLatitude(),
            savedProperty.getLongitude(),
            savedProperty.isNearSchool(),
            savedProperty.isNearPark(),
            savedProperty.isNearTransport()
        );
    }
}