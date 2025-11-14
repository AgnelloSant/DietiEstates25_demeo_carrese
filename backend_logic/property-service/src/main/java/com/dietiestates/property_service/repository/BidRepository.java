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

   
   // @Query(value = "SELECT COUNT(b) FROM Bid b WHERE b.userId = :userId")
    Long countByUserId(@Param("userId") Long userId); 

/*     @Query(value = "SELECT published_at FROM bids WHERE property_id = :idProp ORDER BY published_at DESC LIMIT 1"); 
    String getDateLastBid(@Param("idProp") Long property_id); 
 */

   // @Query(value = "SELECT COUNT(b) FROM Bids b WHERE b.property_id = :property_id", nativeQuery = true)
    Long countByPropertyId(@Param("property_id") Long propertyId);

    @Query(value = "SELECT AVG(b.amount) FROM Bid b WHERE b.propertyId = :propertyId")
    Double findAvgByPropertyId(Long propertyId);

    // @Query("SELECT MAX(b.publishedAt) FROM Bid b WHERE b.propertyId = :propertyId")
    // LocalDateTime findLastDateByProperty(Long propertyId);
    @Query("SELECT MAX(b.publishedAt) FROM Bid b WHERE b.propertyId = :propertyId")
    LocalDateTime findMaxPublishedAtByPropertyId(@Param("propertyId") Long propertyId);
    

   @Query(value = """
    SELECT
        TO_CHAR(b.published_at::date, 'YYYY-MM-DD') AS date_label,
        COUNT(b.id) AS offer_count
    FROM bids b
    INNER JOIN properties p ON b.property_id = p.id  
    WHERE p.user_id = :userId           
    GROUP BY b.published_at::date
    ORDER BY date_label
    """, nativeQuery = true)
List<Object[]> findDailyOfferCountByOwner(@Param("userId") Long userId);
}
