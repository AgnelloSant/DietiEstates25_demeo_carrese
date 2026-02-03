package com.backend.repository;

import com.backend.model.Favourite;
import com.backend.model.FavouriteId;
import com.backend.dto.PropertyDetailDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, FavouriteId> {

    @Query("SELECT new com.backend.dto.PropertyDetailDTO(p.id, p.title, p.city, p.area, p.price) " +
            "FROM Favourite f JOIN Property p ON f.id.propertyId = p.id " +
            "WHERE f.id.userId = :userId " +
            "ORDER BY f.createdAt DESC")
    List<PropertyDetailDTO> findFavouritePropertiesByUserId(@Param("userId") Long userId);

    long countByIdUserId(Long userId);

    void deleteByIdUserIdAndIdPropertyId(Long userId, Long propertyId);
}
