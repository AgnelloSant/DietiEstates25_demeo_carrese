package com.dietiestates.user_service.service;


import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dietiestates.user_service.model.Favourite;
import com.dietiestates.user_service.model.FavouriteId;
import com.dietiestates.user_service.rep.FavouriteRepository;

import com.dietiestates.shared.dto.PropertySearchDTO;

@Service
public class FavouritesLogic {
  
    private final FavouriteRepository favouriteRepository;

    public FavouritesLogic(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    
 @Transactional(readOnly = true)
public List<PropertySearchDTO> getFavouriteProperties(Long userId) {
    return favouriteRepository.findFavouriteRowsByUser(userId).stream()
        .map(r -> new PropertySearchDTO(
            r.getId(),
            r.getTitle(),
            r.getCity(),
            r.getArea(),
            r.getPrice(),
                        false, // default, perché non arrivano dalla query
            false,
            false

        ))
        .toList();
}


    @Transactional(readOnly = true)
    public long countFavourites(Long userId) {
        return favouriteRepository.countByIdUserId(userId);
    }

    @Transactional
    public void addFavouriteProperty(Long userId, Long propId) {
        var id = new FavouriteId(userId, propId);
        if (favouriteRepository.existsById(id)) return;
        try {
            favouriteRepository.save(new Favourite(id));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // vincolo unico già presente → idempotente: ignora
        }
    }

    @Transactional
    public void removeFavourite(Long userId, Long propId) {
        favouriteRepository.deleteByUserAndProp(userId, propId); // 0 o 1 righe → ok
    }
}