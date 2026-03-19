package com.backend;

import com.backend.dto.PropertyUpdateDTO;
import com.backend.model.Property;
import com.backend.repository.PropertyRepository;
import com.backend.service.FileStorageService;
import com.backend.service.GeoapifyService;
import com.backend.service.PropertyService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private GeoapifyService geoapifyService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private PropertyService propertyService;

    // ===============================
    // TC1 - Property non trovata
    // Cammino: N1 -> N2 -> N8
    // ===============================
    @Test
    @DisplayName("TC1 - Property non trovata -> RuntimeException")
    void tc1_propertyNotFound_throwsException() {
        Long id = 1L;

        when(propertyRepository.findById(id)).thenReturn(Optional.empty());

        PropertyUpdateDTO dto = new PropertyUpdateDTO(
                "Titolo",
                "Napoli",
                120.0,
                250000.0,
                LocalDate.of(2025, 1, 10),
                null,
                null,
                false,
                false,
                false,
                "Descrizione",
                "SALE",
                4,
                "A2",
                "Via Roma 1"
        );

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> propertyService.updateProperty(id, dto)
        );

        assertEquals("Property not found with id: 1", ex.getMessage());
        verify(propertyRepository).findById(id);
        verify(propertyRepository, never()).save(any(Property.class));
        verifyNoInteractions(geoapifyService);
    }

    // ===============================
    // TC2 - Property trovata, coordinate assenti
    // Cammino: N1 -> N2 -> N3 -> N4(false) -> N6 -> N7
    // ===============================
    @Test
    @DisplayName("TC2 - Property trovata senza coordinate -> update senza Geoapify")
    void tc2_propertyFound_withoutCoordinates_updatesAndSkipsGeoapify() {
        Long id = 1L;

        Property existing = new Property();
        existing.setId(id);
        existing.setTitle("Vecchio titolo");
        existing.setCity("Caserta");
        existing.setArea(80.0);
        existing.setPrice(150000.0);
        existing.setDescription("Vecchia descrizione");
        existing.setPublishedAt(LocalDate.of(2024, 5, 1));
        existing.setListingType("RENT");
        existing.setRooms(3);
        existing.setEnergyClass("B");
        existing.setAddress("Via Vecchia 10");
        existing.setLatitude(40.0);
        existing.setLongitude(14.0);
        existing.setNearSchool(true);
        existing.setNearPark(true);
        existing.setNearTransport(true);

        PropertyUpdateDTO dto = new PropertyUpdateDTO(
                "Nuovo titolo",
                "Napoli",
                100.0,
                200000.0,
                LocalDate.of(2025, 2, 15),
                null,          // latitude assente
                null,          // longitude assente
                false,
                false,
                false,
                "Nuova descrizione",
                "SALE",
                5,
                "A1",
                "Via Nuova 20"
        );

        when(propertyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PropertyUpdateDTO result = propertyService.updateProperty(id, dto);

        assertNotNull(result);
        assertEquals("Nuovo titolo", result.getTitle());
        assertEquals("Napoli", result.getCity());
        assertEquals(100.0, result.getArea());
        assertEquals(200000.0, result.getPrice());
        assertEquals("Nuova descrizione", result.getDescription());
        assertEquals(LocalDate.of(2025, 2, 15), result.getPublishedAt());
        assertEquals("SALE", result.getListingType());
        assertEquals(5, result.getRooms());
        assertEquals("A1", result.getEnergyClass());
        assertEquals("Via Nuova 20", result.getAddress());

        ArgumentCaptor<Property> propertyCaptor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository).save(propertyCaptor.capture());
        Property saved = propertyCaptor.getValue();

        assertEquals("Nuovo titolo", saved.getTitle());
        assertEquals("Napoli", saved.getCity());
        assertEquals(100.0, saved.getArea());
        assertEquals(200000.0, saved.getPrice());
        assertEquals("Nuova descrizione", saved.getDescription());
        assertEquals(LocalDate.of(2025, 2, 15), saved.getPublishedAt());
        assertEquals("SALE", saved.getListingType());
        assertEquals(5, saved.getRooms());
        assertEquals("A1", saved.getEnergyClass());
        assertEquals("Via Nuova 20", saved.getAddress());

        // Le coordinate precedenti restano invariate perché l'if è false
        assertEquals(40.0, saved.getLatitude());
        assertEquals(14.0, saved.getLongitude());

        verifyNoInteractions(geoapifyService);
    }

    // ===============================
    // TC3 - Property trovata, coordinate presenti
    // Cammino: N1 -> N2 -> N3 -> N4(true) -> N5 -> N6 -> N7
    // ===============================
    @Test
    @DisplayName("TC3 - Property trovata con coordinate -> update con Geoapify")
    void tc3_propertyFound_withCoordinates_updatesAndCallsGeoapify() {
        Long id = 1L;

        Property existing = new Property();
        existing.setId(id);
        existing.setTitle("Vecchio titolo");
        existing.setCity("Caserta");
        existing.setArea(80.0);
        existing.setPrice(150000.0);
        existing.setDescription("Vecchia descrizione");
        existing.setPublishedAt(LocalDate.of(2024, 5, 1));
        existing.setListingType("RENT");
        existing.setRooms(3);
        existing.setEnergyClass("B");
        existing.setAddress("Via Vecchia 10");

        PropertyUpdateDTO dto = new PropertyUpdateDTO(
                "Titolo aggiornato",
                "Roma",
                140.0,
                350000.0,
                LocalDate.of(2025, 3, 20),
                41.9028,
                12.4964,
                false,
                false,
                false,
                "Descrizione aggiornata",
                "SALE",
                6,
                "A4",
                "Via Nazionale 50"
        );

        when(propertyRepository.findById(id)).thenReturn(Optional.of(existing));
        when(geoapifyService.checkNearby(41.9028, 12.4964, "education.school")).thenReturn(true);
        when(geoapifyService.checkNearby(41.9028, 12.4964, "leisure.park")).thenReturn(false);
        when(geoapifyService.checkNearby(41.9028, 12.4964, "public_transport")).thenReturn(true);
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PropertyUpdateDTO result = propertyService.updateProperty(id, dto);

        assertNotNull(result);
        assertEquals("Titolo aggiornato", result.getTitle());
        assertEquals("Roma", result.getCity());
        assertEquals(140.0, result.getArea());
        assertEquals(350000.0, result.getPrice());
        assertEquals(LocalDate.of(2025, 3, 20), result.getPublishedAt());
        assertEquals(41.9028, result.getLatitude());
        assertEquals(12.4964, result.getLongitude());
        assertTrue(result.getNearSchool());
        assertFalse(result.getNearPark());
        assertTrue(result.getNearTransport());

        ArgumentCaptor<Property> propertyCaptor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository).save(propertyCaptor.capture());
        Property saved = propertyCaptor.getValue();

        assertEquals(41.9028, saved.getLatitude());
        assertEquals(12.4964, saved.getLongitude());
        assertTrue(saved.isNearSchool());
        assertFalse(saved.isNearPark());
        assertTrue(saved.isNearTransport());

        verify(geoapifyService).checkNearby(41.9028, 12.4964, "education.school");
        verify(geoapifyService).checkNearby(41.9028, 12.4964, "leisure.park");
        verify(geoapifyService).checkNearby(41.9028, 12.4964, "public_transport");
        verifyNoMoreInteractions(geoapifyService);
    }
}