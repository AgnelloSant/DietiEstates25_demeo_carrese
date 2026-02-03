package com.backend.repository;

import java.util.List;

import com.backend.dto.UserReservation;
import com.backend.model.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByIdProp(Long idProp);

    List<Reservation> findByIdUser(Long idUser);

    Reservation save(@NonNull Reservation reservation);

    @Query(value = "SELECT COUNT(r) FROM Reservation r WHERE r.idUser = :user_id")
    Long countByUserId(@Param("user_id") Long user_id);

    @Query(value = "SELECT COUNT(r) FROM Reservation r WHERE r.idProp = :property_id")
    Long countByPropertyId(@Param("property_id") Long property_id);

    @Query("SELECT new com.backend.dto.UserReservation(" +
            "p.id, " +
            "u.name, " +
            "u.phoneNumber, " +
            "r.date, " +
            "r.time, " +
            "p.title, " +
            "p.address, " +
            "p.city, " +
            "p.listingType, " +
            "p.price) " +
            "FROM Reservation r " +
            "JOIN Property p ON r.idProp = p.id " +
            "JOIN User u ON r.idUser = u.id " +
            "WHERE p.idUser = :ownerId")
    List<UserReservation> getReservationsSummaryByUser(Long ownerId);

}
