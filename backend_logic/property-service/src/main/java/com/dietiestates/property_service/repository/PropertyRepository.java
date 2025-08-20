package com.dietiestates.property_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dietiestates.property_service.model.Property;

//aggiungiamo i metodi crud base come findall(),findById(), save(), deleteById() ecc.)senza doverli
//implementrare manualmente
public interface PropertyRepository extends JpaRepository<Property, Long> {
//COMMUNICA COL DB
//GRAZIE A SPRING
//  case-insensitive sulla città
List<Property> findByCityIgnoreCaseAndAreaGreaterThanEqualAndPriceLessThanEqualOrderByPublishedAtDesc(
    String city, Double minArea, Double maxPrice
);

    //e tramite hibernate dietro le quinte che genera le query sql
}
