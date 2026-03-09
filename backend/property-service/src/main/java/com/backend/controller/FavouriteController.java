package com.backend.controller;

import com.backend.dto.PropertyDetailDTO;
import com.backend.service.FavouriteService;
//import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/favourites")
public class FavouriteController {

    private final FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    // 1. Ottieni la lista dei preferiti
    @GetMapping("/get")
    public ResponseEntity<List<PropertyDetailDTO>> getFavourites(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(favouriteService.getFavouriteProperties(userId));
    }

    // 2. Aggiungi un preferito
    @PostMapping("/add")
    public ResponseEntity<Void> addFavourite(
            @RequestHeader(value = "X-User-Id", required = false) Long userId, 
            @RequestBody Map<String, Long> payload) {
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        Long propId = payload.get("idProp");
        if (propId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        favouriteService.addFavouriteProperty(userId, propId);
        return ResponseEntity.ok().build();
    }

    // 3. Rimuovi un preferito
    @DeleteMapping("/remove/{propId}")
    public ResponseEntity<Void> removeFavourite(
            @RequestHeader(value = "X-User-Id", required = false) Long userId, 
            @PathVariable Long propId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        favouriteService.removeFavourite(userId, propId);
        return ResponseEntity.ok().build();
    }

    // 4. Conteggio preferiti
    @GetMapping("/count")
    public ResponseEntity<Long> countFavourites(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(favouriteService.countFavourites(userId));
    }
}