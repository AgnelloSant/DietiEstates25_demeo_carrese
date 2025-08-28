// src/main/java/com/dietiestates/property_service/repository/PropertyRepository.java
package com.dietiestates.property_service.repository;

import com.dietiestates.property_service.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

  @Query(value = """
  SELECT p.*
  FROM properties p
  WHERE (:city IS NULL OR p.city ILIKE CONCAT('%', :city, '%'))
    AND (:minArea IS NULL OR p.area_mq >= :minArea)
    AND (:maxPrice IS NULL OR p.price    <= :maxPrice)
  ORDER BY p.published_at DESC
""", nativeQuery = true)
List<Property> searchProperties(
    @Param("city") String city,
    @Param("minArea") Double minArea,
    @Param("maxPrice") Double maxPrice
);

}
