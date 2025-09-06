// src/main/java/com/dietiestates/property_service/service/PropertyGetLogic.java
package com.dietiestates.property_service.service;

import com.dietiestates.property_service.dto.PropertyDetailDTO;
import com.dietiestates.property_service.model.Property;
import com.dietiestates.property_service.repository.PropertyRepository;
<<<<<<< HEAD
import com.dietiestates.shared.dto.PropertySearchDTO;
=======
import com.dietiestates.shared.dto.PropertySearchDTO; // ✅ import dal JAR shared

>>>>>>> main-pulito
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PropertyGetLogic {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyGetLogic(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public PropertyDetailDTO getById(Long id) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Property not found with id " + id));

        return new PropertyDetailDTO(
            property.getId(),
            property.getTitle(),
            property.getCity(),
            property.getArea(),
            property.getPrice(),
            property.getDescription(),
            property.getpublishedAt() != null               // ✅ getter corretto (P maiuscola)
                ? property.getpublishedAt().format(DateTimeFormatter.ISO_DATE)
                : null
        );
    }

    public List<PropertySearchDTO> findByIds(List<Long> ids) {
        System.out.println("PropertyGetLogic.findByIds: ");
        if (ids == null || ids.isEmpty()) return List.of();

        // 1) fetch in bulk
        List<Property> props = propertyRepository.findAllById(ids);

        // 2) map Entity -> DTO
        List<PropertySearchDTO> dtos = new ArrayList<>(props.size());
        for (Property p : props) {
            dtos.add(new PropertySearchDTO(
                p.getId(),
                p.getTitle(),
                p.getCity(),
                p.getArea(),
                p.getPrice()
            ));
        }

        // 3) (opzionale ma consigliato) preserva l’ordine degli ids in ingresso
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);
        dtos.sort(Comparator.comparingInt(d -> order.getOrDefault(d.getId(), Integer.MAX_VALUE)));

        return dtos;
    }
}
