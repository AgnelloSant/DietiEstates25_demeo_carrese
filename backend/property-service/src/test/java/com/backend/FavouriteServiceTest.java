package com.backend;

import com.backend.model.Favourite;
import com.backend.model.FavouriteId;
import com.backend.repository.FavouriteRepository;
import com.backend.service.FavouriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @InjectMocks
    private FavouriteService favouriteService;

    // --- TEST PER ADD FAVOURITE ---

    @Test
    void testAddFavourite_PropertyAlreadyFavourite_ShouldReturnEarly() {
        // Arrange
        Long userId = 1L;
        Long propId = 34L;
        var id = new FavouriteId(userId, propId);

        when(favouriteRepository.existsById(id)).thenReturn(true);

        // Act
        favouriteService.addFavouriteProperty(userId, propId);

        // Assert - White-Box: verifichiamo che il flusso si interrompa prima del save
        verify(favouriteRepository, times(1)).existsById(id);
        verify(favouriteRepository, never()).save(any());
    }

    @Test
    void testAddFavourite_PropertyNotFavourite_ShouldSaveSuccessfully() {
        // Arrange
        Long userId = 1L;
        Long propId = 12L;
        var id = new FavouriteId(userId, propId);

        when(favouriteRepository.existsById(id)).thenReturn(false);

        // Act
        favouriteService.addFavouriteProperty(userId, propId);

        // Assert - White-Box: verifichiamo che il flusso attraversi tutto il metodo
        verify(favouriteRepository, times(1)).existsById(id);
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    void testAddFavourite_WhenSaveThrowsException_ShouldHandleGracefully() {
        // Arrange
        Long userId = 1L;
        Long propId = 99L;
        var id = new FavouriteId(userId, propId);

        when(favouriteRepository.existsById(id)).thenReturn(false);
        // Simuliamo un fallimento nel salvataggio
        doThrow(new RuntimeException("Database Error")).when(favouriteRepository).save(any(Favourite.class));

        // Act & Assert - Verifichiamo che l'eccezione sia catturata dal blocco catch
        assertDoesNotThrow(() -> {
            favouriteService.addFavouriteProperty(userId, propId);
        }, "Il servizio deve gestire l'eccezione internamente per garantire l'idempotenza");

        // Verifichiamo che il salvataggio sia stato effettivamente tentato
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }

    // --- TEST PER REMOVE FAVOURITE ---

    @Test
    void testRemoveFavourite_Success() {
        // Arrange
        Long userId = 10L;
        Long propId = 50L;

        // Act
        favouriteService.removeFavourite(userId, propId);

        // Assert - Verifichiamo che i parametri siano passati correttamente al DB
        verify(favouriteRepository, times(1)).deleteByIdUserIdAndIdPropertyId(userId, propId);
    }
}
