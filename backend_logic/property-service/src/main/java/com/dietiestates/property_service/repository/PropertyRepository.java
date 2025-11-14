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
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    AND (:listingType IS NULL OR p.listing_type = :listingType)
    AND (:rooms IS NULL OR p.rooms = :rooms)
    AND (:energyClass IS NULL OR p.energy_class = :energyClass)
  ORDER BY p.published_at DESC
""", nativeQuery = true)
  List<Property> advancedSearch(
      @Param("city") String city,
      @Param("minArea") Double minArea,
      @Param("maxPrice") Double maxPrice,
      @Param("listingType") String listingType,
      @Param("rooms") Integer rooms,
      @Param("energyClass") String energyClass
  );

  //  ricerca per bounding box (solo coordinate)
  //prende le  variabili dal front end e fa la query
@Query(value = """
  SELECT *
  FROM properties p
  WHERE earth_distance(
    ll_to_earth(:lat, :lon),
    ll_to_earth(p.latitude, p.longitude)
  ) <= :radiusKm * 1000
""", nativeQuery = true)
List<Property> searchByBounds(
    @Param("lat") double lat,
    @Param("lon") double lon,
    @Param("radiusKm") double radiusKm
);

  List<Property> findByIdUser(Long idUser);
  
  @Query(value = "UPDATE properties SET views = views + 1 WHERE id = :propertyId", nativeQuery = true)
  void incrementViews(Long propertyId);

}
