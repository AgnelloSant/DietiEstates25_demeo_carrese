package com.dietiestates.property_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.dietiestates.property_service.model.Bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends JpaRepository<Bid, Long>{

    List<Bid> findByPropertyId(Long propertyId);
    List<Bid> findByUserId(Long userId);
    
    Bid findTopByPropertyIdOrderByPublishedAtDesc(Long propertyId);

   
    @Query(value = "SELECT COUNT(b) FROM Bid b WHERE b.userId = :userId", nativeQuery = true)
    Long countByUserId(@Param("userId") Long userId); 

/*     @Query(value = "SELECT published_at FROM bids WHERE property_id = :idProp ORDER BY published_at DESC LIMIT 1"); 
    String getDateLastBid(@Param("idProp") Long property_id); 
 */

    @Query(value = "SELECT COUNT(b) FROM Bids b WHERE b.property_id = :property_id", nativeQuery = true)
    Long countByPropertyId(@Param("property_id") Long propertyId);

    @Query(value = "SELECT AVG(b.amount) FROM Bid b WHERE b.propertyId = :propertyId")
    Double findAvgByPropertyId(Long propertyId);

    @Query("SELECT MAX(b.publishedAt) FROM Bid b WHERE b.propertyId = :propertyId")
    LocalDateTime findLastDateByProperty(Long propertyId);

    
    
}
