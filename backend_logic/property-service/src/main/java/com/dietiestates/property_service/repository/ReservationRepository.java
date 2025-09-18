package com.dietiestates.property_service.repository;

import java.util.List;

import com.dietiestates.property_service.model.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByIdProp(Long idProp);
    List<Reservation> findByIdUser(Long idUser);
    Reservation save(Reservation reservation);

    @Query(value = "SELECT COUNT(r) FROM Reservation r WHERE r.user_id = :user_id", nativeQuery = true)
    Long countByUserId(@Param("user_id") Long user_id);

    @Query(value = "SELECT COUNT(r) FROM Reservation r WHERE r.property_id = :property_id", nativeQuery = true)
    Long countByPropertyId(@Param("property_id") Long property_id); 


}
