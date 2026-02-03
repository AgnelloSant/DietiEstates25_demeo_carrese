package com.backend.repository;

import com.backend.model.Bid;
import com.backend.dto.UserBidsReceived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByPropertyId(Long propertyId);

    List<Bid> findByUserId(Long userId);

    Long countByUserId(Long userId);

    Long countByPropertyId(Long propertyId);

    Bid findTopByPropertyIdOrderByPublishedAtDesc(Long propertyId);

    @Query("SELECT AVG(b.amount) FROM Bid b WHERE b.propertyId = :propertyId")
    Double findAvgByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT MAX(b.publishedAt) FROM Bid b WHERE b.propertyId = :propertyId")
    LocalDateTime findMaxPublishedAtByPropertyId(@Param("propertyId") Long propertyId);

    @Query(value = "SELECT TO_CHAR(b.published_at, 'YYYY-MM-DD'), COUNT(b.id) " +
            "FROM bids b " +
            "JOIN properties p ON b.property_id = p.id " +
            "WHERE p.user_id = :userId " +
            "GROUP BY TO_CHAR(b.published_at, 'YYYY-MM-DD')", nativeQuery = true)
    List<Object[]> findDailyOfferCountByOwner(@Param("userId") Long userId);

    @Query("Select new com.backend.dto.UserBidsReceived(b.id, u.name, u.phoneNumber,  b.amount, b.publishedAt, p.title, p.address, p.city, p.listingType, p.price) "
            +
            "FROM Bid b JOIN User u ON b.userId = u.id JOIN Property p ON b.propertyId = p.id WHERE p.idUser = :userId")
    List<UserBidsReceived> getReceivedBidsToExcel(@Param("userId") Long userId);
}
