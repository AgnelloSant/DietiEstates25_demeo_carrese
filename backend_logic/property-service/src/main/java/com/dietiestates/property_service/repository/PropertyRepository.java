package com.dietiestates.property_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dietiestates.property_service.model.Property;

//aggiungiamo i metodi crud base come findall(),findById(), save(), deleteById() ecc.)senza doverli
//implementrare manualmente
public interface PropertyRepository extends JpaRepository<Property, Long> {
//COMMUNICA COL DB
//GRAZIE A SPRING
//  case-insensitive sulla città
@Query("SELECT p FROM Property p " +
       "WHERE (:city IS NULL OR p.city ILIKE :city) " +
       "AND (:minArea IS NULL OR p.area >= :minArea) " +
       "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
       "ORDER BY p.publishedAt DESC")
List<Property> searchProperties(
        @Param("city") String city,
        @Param("minArea") Double minArea,
        @Param("maxPrice") Double maxPrice
);

    //e tramite hibernate dietro le quinte che genera le query sql
}
