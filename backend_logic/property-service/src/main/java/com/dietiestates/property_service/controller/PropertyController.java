package com.dietiestates.property_service.controller;

import com.dietiestates.property_service.dto.*;
import com.dietiestates.property_service.model.Bid;
import com.dietiestates.property_service.model.Reservation;
import com.dietiestates.property_service.service.*;
import com.dietiestates.shared.dto.PropertySearchDTO;


import com.dietiestates.shared.dto.IdsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertySearchLogic propertySearchLogic;
    private final PropertyCreateLogic propertyCreateLogic;
    private final PropertyUpdateLogic propertyUpdateLogic;
    private final PropertyDeleteLogic propertyDeleteLogic;
    private final PropertyGetLogic propertyGetLogic; 
    private final ReservationService reservationService;
    private final BidService bidService;


    @Autowired
    public PropertyController(
        PropertySearchLogic propertySearchLogic,
        PropertyCreateLogic propertyCreateLogic,
        PropertyUpdateLogic propertyUpdateLogic,
        PropertyDeleteLogic propertyDeleteLogic,
        PropertyGetLogic propertyGetLogic, 
        ReservationService reservationService,
        BidService bidService
    
    ) {
        this.propertySearchLogic = propertySearchLogic;
        this.propertyCreateLogic = propertyCreateLogic;
        this.propertyUpdateLogic = propertyUpdateLogic;
        this.propertyDeleteLogic = propertyDeleteLogic;
        this.propertyGetLogic= propertyGetLogic;
        this.reservationService = reservationService;
        this.bidService = bidService;
      
    }

    // --- Ricerca proprietà ---
@GetMapping("/search")
public ResponseEntity<List<PropertySearchDTO>> searchProperties(
    @RequestParam(required = false) String city,
    @RequestParam(required = false) Double minArea,
    @RequestParam(required = false) Double maxPrice,
    @RequestParam(required = false) String listingType,   // vendita / affitto
    @RequestParam(required = false) Integer rooms,        // numero stanze
    @RequestParam(required = false) String energyClass    // classe energetica
) {
    System.out.println("Cerco in PropertyController: " +
            "city=" + city +
            ", minArea=" + minArea +
            ", maxPrice=" + maxPrice +
            ", listingType=" + listingType +
            ", rooms=" + rooms +
            ", energyClass=" + energyClass);

    List<PropertySearchDTO> results = propertySearchLogic.searchProperties(
        city, minArea, maxPrice, listingType, rooms, energyClass
    );

    return ResponseEntity.ok(results);

}



//ricerca tramite mappa 
@GetMapping("/search-by-bounds")
public ResponseEntity<List<PropertySearchDTO>> searchByBounds(
    @RequestParam Double lat,
    @RequestParam Double lon,
    @RequestParam Double radiusKm
) {
    List<PropertySearchDTO> results = propertySearchLogic.searchByBounds(lat, lon, radiusKm);
    return ResponseEntity.ok(results);
}



    // --- Creazione proprietà ---
@PostMapping("/create")
public ResponseEntity<PropertyCreateDTO> createProperty(@RequestBody PropertyCreateDTO createDTO) {
    System.out.println("📩Create richiesta con DTO: " + createDTO);
    PropertyCreateDTO created = propertyCreateLogic.createProperty(createDTO);
    return ResponseEntity.ok(created);
}



    // --- Aggiornamento proprietà ---
 @PutMapping("/update/{id}")
public ResponseEntity<PropertyUpdateDTO> updateProperty(
        @PathVariable Long id,
        @RequestBody PropertyUpdateDTO updateDTO
) {
    System.out.println("🔄 Update richiesta per ID=" + id + " con DTO: " + updateDTO);
    PropertyUpdateDTO updated = propertyUpdateLogic.updateProperty(id, updateDTO);
    return ResponseEntity.ok(updated);
}

    @PostMapping("/batch")
    public ResponseEntity<List<PropertySearchDTO>> getByIds(@RequestBody IdsRequest req){
        System.out.println("PropertyController.getByIds");
        var out = propertyGetLogic.findByIds(req.getIds());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/updateviews/{id}")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        propertyUpdateLogic.incrementPropertyViews(id);
        return ResponseEntity.ok().build();
    }



    // --- Cancellazione proprietà ---
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        propertyDeleteLogic.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully.");
    }

@GetMapping("/{id}")
public ResponseEntity<PropertyDetailDTO> getProperty(@PathVariable Long id) {
    return ResponseEntity.ok(propertyGetLogic.getById(id)); 
}

//OPERAZIONI SU ANNUNCI 

    // Prenotazioni

    @PostMapping("/reservations/new")
    public ResponseEntity<Boolean> newReservation(
            @RequestBody ReservationCreateDTO reservationCreateDTO,
            @AuthenticationPrincipal Object principal) {

        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());

        return ResponseEntity.ok(
            reservationService.createReservation(
                reservationCreateDTO.getIdProp(), 
                userId,  // 👈 preso dal token, non dal body
                reservationCreateDTO.getDate()
            )
        );
    }

    @GetMapping("/reservations/getbyproperty/{id}")
    public ResponseEntity<List<Reservation>> getReservations(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationsByPropertyId(id));
    } 

    @GetMapping("/reservations/getbyuser/me")
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

    @GetMapping("/reservations/count/me")
    public ResponseEntity<Long> countReservationsByUser(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        return ResponseEntity.ok(reservationService.countReservationsByUser(userId));
    }

    @GetMapping("/reservations/countByProperty/{idProperty}")
    public ResponseEntity<Long> countReservationsByProperty(@PathVariable Long idProperty){ 
        return ResponseEntity.ok(reservationService.countReservationByProperty(idProperty));
    }


    // Offerte

    @GetMapping("/bids/getbyproperty/{id}")
    public ResponseEntity<List<Bid>> getBid(@PathVariable Long id) {
        return ResponseEntity.ok(bidService.getBidsByPropertyId(id));
    }

    @GetMapping("/bids/countByProperty/{id}")
    public ResponseEntity<Long> countBidsByProperty(@PathVariable Long id){ 
        return ResponseEntity.ok(bidService.countBidsByPropertyId(id)); 
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

    @GetMapping("/bids/count/me")
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
    public ResponseEntity<String> getLastBid(@PathVariable Long idProp){
        Bid bid = bidService.getDateLastBid(idProp); 
        String date = bid != null ? bid.getPublishedAt() : null;
        return ResponseEntity.ok(date); 
    }




}
