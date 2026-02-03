package com.backend.service;

import com.backend.dto.PropertyDetailDTO;
import com.backend.model.Property;
import com.backend.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class SearchService {

    private final PropertyRepository propertyRepository;
    private final FileStorageService fileStorageService;

    public SearchService(PropertyRepository propertyRepository, FileStorageService fileStorageService) {
        this.propertyRepository = propertyRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<PropertyDetailDTO> searchProperties(
            String city,
            Double minArea,
            Double maxPrice,
            String listingType,
            Integer rooms,
            String energyClass) {
        List<Property> properties = propertyRepository.advancedSearch(
                city, minArea, maxPrice, listingType, rooms, energyClass);

        return properties.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<PropertyDetailDTO> searchByBounds(Double lat, Double lon, Double radiusKm) {
        List<Property> properties = propertyRepository.searchByBounds(lat, lon, radiusKm);

        return properties.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PropertyDetailDTO convertToDto(Property property) {
        return new PropertyDetailDTO(
                property.getId(),
                property.getTitle(),
                property.getCity(),
                property.getArea(),
                property.getPrice(),
                property.isNearSchool(),
                property.isNearPark(),
                property.isNearTransport(),
                property.getDescription(),
                property.getPublishedAt(),
                property.getListingType(),
                property.getRooms(),
                property.getEnergyClass(),
                property.getAddress(),
                property.getIdUser(),
                property.getViews(),
                property.getLatitude(),
                property.getLongitude(),
                fileStorageService.getImages(property.getId()).stream().findFirst().orElse(null));
    }
}
