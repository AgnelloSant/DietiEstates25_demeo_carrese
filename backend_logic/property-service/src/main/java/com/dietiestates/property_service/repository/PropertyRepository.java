package com.dietiestates.property_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dietiestates.property_service.model.Property;


public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByCityAndAreaGreaterThanEqualAndPriceLessThanEqualOrderByPublishedAtDesc(
        String city, Double minArea, Double maxPrice
    );
}
