package com.backend.service;

import com.backend.repository.ReservationRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.model.Reservation;
import java.time.LocalDate;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Boolean createReservation(Long idProp, Long idUser, String date, String time) {
        try {
            LocalDate reservationDate = LocalDate.parse(date);
            if (reservationDate.isBefore(LocalDate.now())) {
                return false;
            }
            Reservation reservation = new Reservation();
            reservation.setIdProp(idProp);
            reservation.setIdUser(idUser);
            reservation.setDate(date);
            reservation.setTime(time);
            reservationRepository.save(reservation);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getReservationsByPropertyId(Long idProp) {
        return reservationRepository.findByIdProp(idProp);
    }

    public List<Reservation> getReservationsByUserId(Long idUser) {
        return reservationRepository.findByIdUser(idUser);
    }

    public boolean deleteReservation(Long id) {

        // TODO: implementare eliminazione prenotazione
        return true; // Ritorna true se l'eliminazione è avvenuta con successo
    }

    public Long countReservationsByUser(Long user_id) {
        return reservationRepository.countByUserId(user_id);
    }

    public Long countReservationByProperty(Long property_id) {
        return reservationRepository.countByPropertyId(property_id);
    }

}
