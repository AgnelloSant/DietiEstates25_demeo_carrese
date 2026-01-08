package com.dietiestates.property_service.model;

import com.dietiestates.property_service.controller.PropertyController;
import com.dietiestates.property_service.dto.*;
import com.dietiestates.property_service.service.*;
import com.dietiestates.shared.dto.PropertySearchDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

// Import statici per JUnit 5
import static org.junit.jupiter.api.Assertions.assertThrows;

// Import statici per Mockito
import static org.mockito.Mockito.*;

// IMPORT STATICI PER HAMCREST (La parte fondamentale)
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    // --- MOCKS DEI SERVIZI ---
    @Mock private PropertySearchLogic propertySearchLogic;
    @Mock private PropertyCreateLogic propertyCreateLogic;
    @Mock private PropertyUpdateLogic propertyUpdateLogic;
    @Mock private PropertyDeleteLogic propertyDeleteLogic;
    @Mock private PropertyGetLogic propertyGetLogic;
    @Mock private ReservationService reservationService;
    @Mock private BidService bidService;

    @InjectMocks
    private PropertyController propertyController;

    // ===================================================================================
    // METODO 1: searchByBounds (Ricerca Geospaziale)
    // Strategia: Boundary Value Analysis (Robustness) -> 19 Test Case
    // Copertura: Limiti validi e invalidi per Latitudine, Longitudine e Raggio.
    // ===================================================================================

    @ParameterizedTest(name = "BVA Robustness {index}: Lat={0}, Lon={1}, Radius={2} -> Atteso: {3}")
    @CsvSource({
        // --- BASE CASE (Tutti Nominali) ---
        "45.0, 10.0, 50.0, VALID",     // 1. Nominale

        // --- VARIABILE 1: LATITUDE (Range -90 a 90) ---
        "-90.0, 10.0, 50.0, VALID",    // 2. Min
        "-89.9, 10.0, 50.0, VALID",    // 3. Min+
        "89.9,  10.0, 50.0, VALID",    // 4. Max-
        "90.0,  10.0, 50.0, VALID",    // 5. Max
        "-90.1, 10.0, 50.0, INVALID",  // 6. Min- (Errore)
        "90.1,  10.0, 50.0, INVALID",  // 7. Max+ (Errore)

        // --- VARIABILE 2: LONGITUDE (Range -180 a 180) ---
        "45.0, -180.0, 50.0, VALID",   // 8. Min
        "45.0, -179.9, 50.0, VALID",   // 9. Min+
        "45.0, 179.9,  50.0, VALID",   // 10. Max-
        "45.0, 180.0,  50.0, VALID",   // 11. Max
        "45.0, -180.1, 50.0, INVALID", // 12. Min- (Errore)
        "45.0, 180.1,  50.0, INVALID", // 13. Max+ (Errore)

        // --- VARIABILE 3: RADIUS (Range > 0) ---
        "45.0, 10.0, 0.1,     VALID",   // 14. Min
        "45.0, 10.0, 0.2,     VALID",   // 15. Min+
        "45.0, 10.0, 19999.0, VALID",   // 16. Max-
        "45.0, 10.0, 20000.0, VALID",   // 17. Max
        "45.0, 10.0, -0.1,    INVALID", // 18. Min- (Negativo -> Errore)
        "45.0, 10.0, 20001.0, INVALID"  // 19. Max+ (Troppo grande -> Errore)
    })

    void testSearchByBounds_Robustness(Double lat, Double lon, Double radius, String expectedResult) {

        if ("VALID".equals(expectedResult)) {
            // SETUP MOCK
            PropertySearchDTO mockProp = new PropertySearchDTO();
            mockProp.setId(1L);

            when(propertySearchLogic.searchByBounds(lat, lon, radius))
                    .thenReturn(List.of(mockProp));//creiamo liksta finta da cui il service dovrà attingere
            
            // ESECUZIONE
            ResponseEntity<List<PropertySearchDTO>> response = 
                    propertyController.searchByBounds(lat, lon, radius);

            // ASSERZIONI CON HAMCREST
            // Verifica Status Code
            assertThat("Status code deve essere 200 OK", 
                  response.getStatusCode().value(), is(200));
            
            // Verifica Body
            assertThat("Il body non deve essere nullo", 
                    response.getBody(), notNullValue());
            assertThat("La lista deve contenere 1 elemento", 
                    response.getBody(), hasSize(1));
            
        } else {
            // CASO INVALIDO
            when(propertySearchLogic.searchByBounds(lat, lon, radius))
                    .thenThrow(new IllegalArgumentException("Invalid input"));
            
            assertThrows(IllegalArgumentException.class, () -> { //qui controlla se davvero il controll lanci l exception in  questi casi 
                propertyController.searchByBounds(lat, lon, radius);
            });
        }
    }

    // ===================================================================================
    // METODO 2: searchProperties (Ricerca Filtri)
    // Strategia: N-WECT (ISP Signature-Based) -> Max Classi = 9 Test Case
    // Copertura: Tutte le classi energetiche + Each Choice su tutti gli altri parametri.
    // ===================================================================================

    @ParameterizedTest(name = "N-WECT {index}: Energy={5}, Type={3} ... -> {6}")
    @CsvSource(value = {
        // 1. Energy A (Valid) | Type VENDITA (Valid) | Full Params
        "Napoli, 50.0, 200000.0, vendita, 3, A, VALID",

        // 2. Energy B (Valid) | Type AFFITTO (Valid) | Null Params (Opzionali)
        "null, null, null, affitto, null, B, VALID",

        // 3. Energy C (Valid) | Type Null (Valid) | Area Invalid
        "Roma, -10.0, 150000.0, null, 2, C, INVALID", 

        // 4. Energy D (Valid) | Type Invalid (Scambio)
        "Milano, 60.0, 250000.0, scambio, 4, D, INVALID", 

        // 5. Energy E (Valid) | Type VENDITA | Rooms Invalid (Negativo)
        "Torino, 70.0, 100000.0, vendita, -1, E, INVALID",

        // 6. Energy F (Valid) | Price Invalid (Negativo)
        "Firenze, 80.0, -100.0, affitto, 5, F, INVALID",

        // 7. Energy G (Valid) | All Valid
        "Bologna, 40.0, 120000.0, vendita, 1, G, VALID",

        // 8. Energy Null (Valid/Opzionale) | All Valid
        "null, 100.0, 500000.0, affitto, 4, null, VALID",

        // 9. Energy Invalid (Z)
        "Napoli, 50.0, 200000.0, vendita, 3, Z, INVALID"
    }, nullValues = "null")

    void testSearchProperties_NWECT(        //prende i test da 1 a 9 uno alla volta e li inserisce nelle corrette variabili
            String city, Double minArea, Double maxPrice, String type, Integer rooms, String energy, String expected) {

        if ("VALID".equals(expected)) {
            // SETUP MOCK(preparazione)
            PropertySearchDTO resultDto = new PropertySearchDTO();
            resultDto.setCity(city != null ? city : "AnyCity"); // Valore di default per il mock
            
            when(propertySearchLogic.searchProperties(city, minArea, maxPrice, type, rooms, energy)) //quando qualcono chiamera search properties
                    .thenReturn(List.of(resultDto));  //lista finta,non controlla nel db! ma in questa lista finta appena create grazie a csv

            // ESECUZIONE
            ResponseEntity<List<PropertySearchDTO>> response = 
                    propertyController.searchProperties(city, minArea, maxPrice, type, rooms, energy);//avvia la search 

            // ASSERZIONI CON HAMCREST
          assertThat(response.getStatusCode().value(), is(200));//controlliao se è invalid
            
            List<PropertySearchDTO> body = response.getBody();//estraiamo il corpo della risposta

            assertThat(body, is(notNullValue()));
            assertThat(body, is(not(empty()))); // Verifica che la lista non sia vuota
            assertThat(body, hasSize(1));
            
            // Verifica profonda sul contenuto del DTO restituito
            assertThat(body.get(0).getCity(), equalTo(resultDto.getCity()));  //l elemento è proprio quello che mi aspettavo?

        } else {
            // CASO INVALIDO
            when(propertySearchLogic.searchProperties(city, minArea, maxPrice, type, rooms, energy))
                    .thenThrow(new IllegalArgumentException("Invalid param"));

            assertThrows(IllegalArgumentException.class, () -> 
                propertyController.searchProperties(city, minArea, maxPrice, type, rooms, energy));
        }
    }
}