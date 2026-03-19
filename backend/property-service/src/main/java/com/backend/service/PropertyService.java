package com.backend.service;

import org.springframework.stereotype.Service;
import com.backend.dto.PropertyCreateDTO;
import com.backend.dto.PropertyUpdateDTO;
import com.backend.dto.PropertyDetailDTO;
import com.backend.model.Property;
import com.backend.repository.PropertyRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final GeoapifyService geoapifyService;
    private final FileStorageService fileStorageService;

    public PropertyService(PropertyRepository propertyRepository, GeoapifyService geoapifyService,
            FileStorageService fileStorageService) {
        this.propertyRepository = propertyRepository;
        this.geoapifyService = geoapifyService;
        this.fileStorageService = fileStorageService;
    }

    public PropertyCreateDTO createProperty(PropertyCreateDTO createDTO) {
        Property property = new Property();
        property.setTitle(createDTO.getTitle());
        property.setCity(createDTO.getCity());
        property.setArea(createDTO.getArea());
        property.setPrice(createDTO.getPrice());
        property.setDescription(createDTO.getDescription());
        property.setPublishedAt(createDTO.getPublishedAt()); // LocalDate
        property.setLatitude(createDTO.getLatitude());
        property.setLongitude(createDTO.getLongitude());
        property.setListingType(createDTO.getListingType());
        property.setRooms(createDTO.getRooms());
        property.setEnergyClass(createDTO.getEnergyClass());
        property.setAddress(createDTO.getAddress());
        property.setIdUser(createDTO.getIdUser());
        property.setViews(0L);

        // Geocoding logic
        if (property.getLatitude() == null || property.getLongitude() == null) {
            String fullAddress = (createDTO.getAddress() != null ? createDTO.getAddress() : "")
                    + " " + (createDTO.getCity() != null ? createDTO.getCity() : "");
            double[] coords = geoapifyService.getCoordinates(fullAddress.trim());
            if (coords != null) {
                property.setLatitude(coords[0]);
                property.setLongitude(coords[1]);
            }
        }

        // POI logic
        if (property.getLatitude() != null && property.getLongitude() != null) {
            property.setNearSchool(
                    geoapifyService.checkNearby(property.getLatitude(), property.getLongitude(), "education.school"));
            property.setNearPark(
                    geoapifyService.checkNearby(property.getLatitude(), property.getLongitude(), "leisure.park"));
            property.setNearTransport(
                    geoapifyService.checkNearby(property.getLatitude(), property.getLongitude(), "public_transport"));
        }

        propertyRepository.save(property);
        createDTO.setId(property.getId());
        createDTO.setNearSchool(property.isNearSchool());
        createDTO.setNearPark(property.isNearPark());
        createDTO.setNearTransport(property.isNearTransport());

        return createDTO;
    }

    public List<PropertyDetailDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PropertyDetailDTO> getPropertyById(Long id) {
        System.out.println("DEBUG PropertyService: getPropertyById called for id: " + id);
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        System.out.println("DEBUG PropertyService: propertyOpt: " + propertyOpt);
        if (propertyOpt.isPresent()) {
            Property p = propertyOpt.get();
            System.out.println("DEBUG PropertyService: p: " + p);

            // propertyRepository.incrementViews(id);
            p.setViews(p.getViews() + 1);

            return Optional.of(convertToDto(p));
        }
        return Optional.empty();
    }

    private PropertyDetailDTO convertToDto(Property property) {
        List<String> images = fileStorageService.getImages(property.getId());
        String mainImage = images.isEmpty() ? null : images.get(0);

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
                property.getPublishedAt(), // LocalDate
                property.getListingType(),
                property.getRooms(),
                property.getEnergyClass(),
                property.getAddress(),
                property.getIdUser(),
                property.getViews(),
                property.getLatitude(),
                property.getLongitude(),
                mainImage);
    }

    public List<PropertyDetailDTO> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return List.of();

        List<Property> props = propertyRepository.findAllById(ids);
        List<PropertyDetailDTO> dtos = new ArrayList<>(props.size());
        for (Property p : props) {
            dtos.add(convertToDto(p));
        }

        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++)
            order.put(ids.get(i), i);
        dtos.sort(Comparator.comparingInt(d -> order.getOrDefault(d.getId(), Integer.MAX_VALUE)));

        return dtos;
    }

    public PropertyUpdateDTO updateProperty(Long id, PropertyUpdateDTO updateDTO) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        property.setTitle(updateDTO.getTitle());
        property.setCity(updateDTO.getCity());
        property.setArea(updateDTO.getArea());
        property.setPrice(updateDTO.getPrice());
        property.setDescription(updateDTO.getDescription());
        property.setPublishedAt(updateDTO.getPublishedAt()); // LocalDate
        property.setListingType(updateDTO.getListingType());
        property.setRooms(updateDTO.getRooms());
        property.setEnergyClass(updateDTO.getEnergyClass());
        property.setAddress(updateDTO.getAddress());

        // Coordinates update logic if provided
        if (updateDTO.getLatitude() != null && updateDTO.getLongitude() != null) {
            property.setLatitude(updateDTO.getLatitude());
            property.setLongitude(updateDTO.getLongitude());
            // Re-check POIs?
            property.setNearSchool(
                    geoapifyService.checkNearby(property.getLatitude(), property.getLongitude(), "education.school"));
            property.setNearPark(
                    geoapifyService.checkNearby(property.getLatitude(), property.getLongitude(), "leisure.park"));
            property.setNearTransport(
                    geoapifyService.checkNearby(property.getLatitude(), property.getLongitude(), "public_transport"));
        }

        Property updatedProperty = propertyRepository.save(property);

        return new PropertyUpdateDTO(
                updatedProperty.getTitle(),
                updatedProperty.getCity(),
                updatedProperty.getArea(),
                updatedProperty.getPrice(),
                updatedProperty.getPublishedAt(),
                updatedProperty.getLatitude(),
                updatedProperty.getLongitude(),
                updatedProperty.isNearSchool(),
                updatedProperty.isNearPark(),
                updatedProperty.isNearTransport(),
                updatedProperty.getDescription(),
                updatedProperty.getListingType(),
                updatedProperty.getRooms(),
                updatedProperty.getEnergyClass(),
                updatedProperty.getAddress());
    }


public void deleteProperty(Long id) {
    if (!propertyRepository.existsById(id)) {
        throw new RuntimeException("Property not found with id: " + id);
    }

    fileStorageService.deletePropertyImages(id);
    propertyRepository.deleteById(id);
}

    public void incrementPropertyViews(Long propertyId) {
        propertyRepository.incrementViews(propertyId);
    }

    public void uploadImage(org.springframework.web.multipart.MultipartFile file, Long propertyId) {
        fileStorageService.saveImage(file, propertyId);
    }

    public List<String> getPropertyImages(Long propertyId) {
        return fileStorageService.getImages(propertyId);
    }





}
