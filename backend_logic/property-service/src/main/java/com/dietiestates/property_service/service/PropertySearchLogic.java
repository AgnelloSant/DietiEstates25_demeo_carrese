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

    /**
     *  Ricerca avanzata delle proprietà filtrata per:
     * - città (case-insensitive, opzionale)
     * - superficie minima (opzionale)
     * - prezzo massimo (opzionale)
     * - tipologia di inserzione: vendita / affitto (opzionale)
     * - numero di stanze (opzionale)
     * - classe energetica (opzionale)
     *
     * Restituisce una lista di PropertySearchDTO.
     */
    public List<PropertySearchDTO> searchProperties(
            String city,
            Double minArea,
            Double maxPrice,
            String listingType,
            Integer rooms,
            String energyClass
    ) {
        System.err.println("PropertySearchLogic.searchProperties: city=" + city +
                ", minArea=" + minArea +
                ", maxPrice=" + maxPrice +
                ", listingType=" + listingType +
                ", rooms=" + rooms +
                ", energyClass=" + energyClass);

        // Query avanzata sul repository
        List<Property> properties = propertyRepository.advancedSearch(
                city, minArea, maxPrice, listingType, rooms, energyClass
        );

        // Mapping Entity → DTO
        return properties.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 🔎 Ricerca geografica per raggio .
     * Vengono restituite tutte le proprietà entro un certo raggio in km
     * da un punto centrale (lat, lon).
     *
     * @param lat       Latitudine del centro
     * @param lon       Longitudine del centro
     * @param radiusKm  Raggio di ricerca in km
     */
    public List<PropertySearchDTO> searchByBounds(double lat, double lon, double radiusKm) {
        List<Property> props = propertyRepository.searchByBounds(lat, lon, radiusKm);

        // Mapping Entity → DTO
        return props.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     *  Conversione singola Property → DTO
     * Include latitudine e longitudine per la mappa
     */
    private PropertySearchDTO convertToDto(Property property) {
        return new PropertySearchDTO(
                property.getId(),
                property.getTitle(),
                property.getCity(),
                property.getArea(),
                property.getPrice(),
                property.isNearSchool(),
                property.isNearPark(),
                property.isNearTransport(),
                property.getListingType(),
                property.getRooms(),
                property.getEnergyClass(),
                property.getAddress(),
                property.getLatitude(),
                property.getLongitude()
        );
    }
}
