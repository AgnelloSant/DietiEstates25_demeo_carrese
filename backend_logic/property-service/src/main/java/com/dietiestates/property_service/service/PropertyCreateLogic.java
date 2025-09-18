
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
    private final GeoapifyService geoapifyService;

    public PropertyCreateLogic(PropertyRepository propertyRepository, GeoapifyService geoapifyService) {
        this.propertyRepository = propertyRepository;
        this.geoapifyService = geoapifyService;
    }

    public PropertyCreateDTO createProperty(PropertyCreateDTO createDTO) {
        Property property = new Property();
        property.setTitle(createDTO.getTitle());
        property.setCity(createDTO.getCity());
        property.setAddress(createDTO.getAddress());
        property.setArea(createDTO.getArea());
        property.setPrice(createDTO.getPrice());
        property.setPublishedAt(LocalDate.now());

        property.setListingType(createDTO.getListingType());
        property.setRooms(createDTO.getRooms());
        property.setEnergyClass(createDTO.getEnergyClass());

        // --- Gestione coordinate ---
        Double lat = createDTO.getLatitude();
        Double lon = createDTO.getLongitude();

        if ((lat == null || lon == null) && createDTO.getAddress() != null && !createDTO.getAddress().isBlank()) {
            Map<String, Double> coords = geoapifyService.geocodeAddress(createDTO.getAddress());
            if (!coords.isEmpty()) {
                lat = coords.get("lat");
                lon = coords.get("lon");
            }
        }

        property.setLatitude(lat);
        property.setLongitude(lon);

        // --- Servizi vicini ---
        if (lat != null && lon != null) {
            Map<String, Boolean> features = geoapifyService.checkNearbyPlaces(lat, lon);
            property.setNearSchool(features.get("nearSchool"));
            property.setNearPark(features.get("nearPark"));
            property.setNearTransport(features.get("nearTransport"));
        } else {
            property.setNearSchool(false);
            property.setNearPark(false);
            property.setNearTransport(false);
        }

        Property savedProperty = propertyRepository.save(property);

        return new PropertyCreateDTO(
            savedProperty.getTitle(),
            savedProperty.getCity(),
            savedProperty.getArea(),
            savedProperty.getPrice(),
            savedProperty.getPublishedAt(),
            savedProperty.getLatitude(),
            savedProperty.getLongitude(),
            savedProperty.isNearSchool(),
            savedProperty.isNearPark(),
            savedProperty.isNearTransport(),
            savedProperty.getListingType(),
            savedProperty.getRooms(),
            savedProperty.getEnergyClass(),
            savedProperty.getAddress()
        );
    }
}
