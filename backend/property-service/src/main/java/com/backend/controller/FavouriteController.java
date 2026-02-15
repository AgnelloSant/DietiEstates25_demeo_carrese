package com.backend.controller;

import com.backend.dto.PropertyDetailDTO;
import com.backend.service.FavouriteService;
//import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/favourites")
public class FavouriteController {

    private final FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<PropertyDetailDTO>> getFavourites(@RequestHeader("X-User-Id") Long userId) {

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(favouriteService.getFavouriteProperties(userId));
        } catch (NumberFormatException e) {
            System.err.println("ERROR PropertyService: Invalid user ID format: " + userId);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addFavourite(@AuthenticationPrincipal Object principal,
            @RequestBody java.util.Map<String, Long> payload) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        Long propId = payload.get("idProp");
        if (propId == null) {
            return ResponseEntity.badRequest().build();
        }
        favouriteService.addFavouriteProperty(userId, propId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{propId}")
    public ResponseEntity<Void> removeFavourite(@AuthenticationPrincipal Object principal, @PathVariable Long propId) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        favouriteService.removeFavourite(userId, propId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countFavourites(@AuthenticationPrincipal Object principal) {
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(principal.toString());
        return ResponseEntity.ok(favouriteService.countFavourites(userId));
    }
}
