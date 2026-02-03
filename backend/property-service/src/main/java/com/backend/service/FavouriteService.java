package com.backend.service;

import com.backend.model.Favourite;
import com.backend.dto.PropertyDetailDTO;
import com.backend.model.FavouriteId;
import com.backend.repository.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;

    public FavouriteService(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    @Transactional(readOnly = true)
    public List<PropertyDetailDTO> getFavouriteProperties(Long userId) {
        return favouriteRepository.findFavouritePropertiesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countFavourites(Long userId) {
        return favouriteRepository.countByIdUserId(userId);
    }

    @Transactional
    public void addFavouriteProperty(Long userId, Long propId) {
        var id = new FavouriteId(userId, propId);
        if (favouriteRepository.existsById(id))
            return;
        try {
            favouriteRepository.save(new Favourite(id));
        } catch (Exception e) {
            // Already exists or other error, ignore for idempotency
        }
    }

    @Transactional
    public void removeFavourite(Long userId, Long propId) {
        favouriteRepository.deleteByIdUserIdAndIdPropertyId(userId, propId);
    }
}
