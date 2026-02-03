package com.backend.controller;

import com.backend.dto.BidSummaryDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Fix for @AuthenticationPrincipal
import org.springframework.http.ResponseEntity; // Assuming this might be missing too, or was implicitly there? The file used it. I should check if ResponseEntity was imported.
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.format.DateTimeFormatter; // Used in getLastBid

import com.backend.service.ReservationService;
import com.backend.service.BidService;
import com.backend.dto.ReservationCreateDTO;
import com.backend.dto.UserBidsReceived;
import com.backend.model.Reservation;
import com.backend.dto.BidCreateDTO;
import com.backend.model.Bid;
import com.backend.dto.UserReservation;
import com.backend.service.ExcelService;
//import com.backend.dto.BidSummaryDTO;
import com.backend.dto.BidTrendDTO;

import java.io.ByteArrayInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.sql.Date;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;

@RestController
@RequestMapping("")
public class OperationController {

    private final ReservationService reservationService;
    private final BidService bidService;
    private final ExcelService excelService;

    public OperationController(ReservationService reservationService, BidService bidService,
            ExcelService excelService) {
        this.reservationService = reservationService;
        this.bidService = bidService;
        this.excelService = excelService;
    }

    // prenotazioni

    @PostMapping("/reservations/new")
    public ResponseEntity<Boolean> newReservation(
            @RequestBody ReservationCreateDTO reservationCreateDTO,
            @AuthenticationPrincipal Object principal) {

        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        System.out.println("DEBUG -> Ricevuta prenotazione per idProp: " + reservationCreateDTO.getIdProp());

        if (reservationCreateDTO.getIdProp() == null) {
            System.err.println("Errore: idProp è nullo nel backend!");
            return ResponseEntity.badRequest().build();
        }

        Long userId = Long.valueOf(principal.toString());

        return ResponseEntity.ok(
                reservationService.createReservation(
                        reservationCreateDTO.getIdProp(),
                        userId, // 👈 preso dal token, non dal body
                        reservationCreateDTO.getDate(),
                        reservationCreateDTO.getTime()));
    }

    @GetMapping("/reservations/getbyproperty/{id}")
    public ResponseEntity<List<Reservation>> getReservations(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationsByPropertyId(id));
    }

    @GetMapping("/reservations/getbyuser")
    public ResponseEntity<List<Reservation>> getUserReservations(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }

    @DeleteMapping("/reservations/delete/{id}")
    public ResponseEntity<Boolean> deleteReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.deleteReservation(id));
    }

    @GetMapping("/reservations/count")
    public ResponseEntity<Long> countReservationsByUser(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        return ResponseEntity.ok(reservationService.countReservationsByUser(userId));
    }

    @GetMapping("/reservations/countbyproperty/{idProp}")
    public ResponseEntity<Long> countReservationsByProperty(@PathVariable Long idProp) {
        return ResponseEntity.ok(reservationService.countReservationByProperty(idProp));
    }

    // Offerte

    @GetMapping("/bids/getbyproperty/{idProp}")
    public ResponseEntity<List<Bid>> getBid(@PathVariable Long idProp) {
        return ResponseEntity.ok(bidService.getBidsByPropertyId(idProp));
    }

    @GetMapping("/bids/countbyproperty/{idProp}")
    public ResponseEntity<Long> countBidsByProperty(@PathVariable Long idProp) {
        return ResponseEntity.ok(bidService.countBidsByPropertyId(idProp));
    }

    @GetMapping("/bids/getbyuser")
    public ResponseEntity<List<Bid>> getUserBids(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        return ResponseEntity.ok(bidService.getBidsByUserId(userId));
    }

    @PostMapping("/bids/new")
    public ResponseEntity<Boolean> newBid(
            @RequestBody BidCreateDTO bidCreateDTO,
            @AuthenticationPrincipal Object principal) {

        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        return ResponseEntity.ok(bidService.placeBid(bidCreateDTO, userId));
    }

    @DeleteMapping("/bids/delete/{id}")
    public ResponseEntity<Boolean> deleteOffer(@PathVariable Long id) {
        return ResponseEntity.ok(bidService.deleteBid(id));
    }

    @GetMapping("/bids/count")
    public ResponseEntity<Long> countBidsByUser(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        return ResponseEntity.ok(bidService.countBidsByUserId(userId));
    }

    @GetMapping("/bids/getsummary")
    public ResponseEntity<List<BidSummaryDTO>> getBidSummary(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        System.out.println("ID utente: " + userId);
        return ResponseEntity.ok(bidService.getBidsSummaryByUser(userId));
    }

    @GetMapping("/bids/getlast/{idProp}")
    public ResponseEntity<String> getLastBid(@PathVariable Long idProp) {
        Bid bid = bidService.getDateLastBid(idProp);
        if (bid == null || bid.getPublishedAt() == null) {
            return ResponseEntity.notFound().build();
        }

        LocalDateTime publishedAt = bid.getPublishedAt();
        String dateString = publishedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return ResponseEntity.ok(dateString);
    }

    @GetMapping("/bids/getmonthlytrend")
    public ResponseEntity<List<BidTrendDTO>> getMonthlyTrend(@AuthenticationPrincipal Object principal) {

        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());

        List<BidTrendDTO> trendData = bidService.findDailyOfferCount(userId);

        System.out.println("Trend Data: " + trendData);

        return ResponseEntity.ok(trendData);
    }

    @GetMapping("/reservations/getexcel")
    public ResponseEntity<Resource> getReservationExcel(
            @RequestHeader(value = "X-User-Id", required = true) String userIdString) {
        Long userId;
        try {
            userId = Long.valueOf(userIdString);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).build();
        }

        // Devo cercare le prenotazioni di tutte le proprietà di cui l'utente è
        // proprietario
        List<UserReservation> reservations = excelService.getReservationsSummaryByUser(userId);

        ByteArrayInputStream in = excelService.loadReservationsToExcel(reservations);

        String dateToday = java.time.LocalDate.now().toString(); // Restituisce "2026-02-02"
        String fileName = "Prenotazioni_" + dateToday + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/bids/getreceived")
    public ResponseEntity<Resource> getReceivedBids(
            @RequestHeader(value = "X-User-Id", required = true) String userIdString) {
        Long userId;
        try {
            userId = Long.valueOf(userIdString);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).build();
        }

        List<UserBidsReceived> bids = excelService.getReceivedBidsToExcel(userId);

        ByteArrayInputStream in = excelService.loadReceivedBidsToExcel(bids);

        String dateToday = java.time.LocalDate.now().toString(); // Restituisce "2026-02-02"
        String fileName = "Offerte_" + dateToday + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

}
