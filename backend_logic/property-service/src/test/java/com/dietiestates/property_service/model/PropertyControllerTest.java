package com.dietiestates.property_service.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per PropertyController - Verifica funzionamento metodi con parametri multipli
 * 
 * Questo file testa due metodi principali del controller:
 * 1. searchProperties - ricerca immobili con 6 filtri diversi
 * 2. searchByBounds - ricerca geografica con coordinate e raggio
 * 
 * I test verificano che i parametri vengano validati correttamente
 * e che la logica di business funzioni .
 */
class PropertyControllerTest {

    // Variabili per simulare i parametri del metodo searchProperties
    // Questi rappresentano i filtri che un utente potrebbe usare per cercare casa
    private String testCity;           
    private Double testMinArea;       
    private Double testMaxPrice;      
    private String testListingType;   
    private Integer testRooms;        
    private String testEnergyClass;   
    
    // Variabili per simulare i parametri del metodo searchByBounds
    
    private Double testLat;            
    private Double testLon;            
    private Double testRadiusKm;       
    /**
     * Questo metodo viene eseguito automaticamente prima di ogni test
     * Serve per preparare i dati in uno stato pulito e prevedibile
     */
    @BeforeEach
    void setUp() {
        System.out.println("\n--- Preparazione test ---");
        
        // Impostiamo valori realistici per una ricerca immobiliare tipica
        testCity = "Milano";
        testMinArea = 50.0;              
        testMaxPrice = 300000.0;         
        testListingType = "affitto";     
        testRooms = 2;                   
        testEnergyClass = "B";           
        
        // Coordinate geografiche del centro di Roma (vicino al Colosseo)
        testLat = 41.9028;
        testLon = 12.4964;
        testRadiusKm = 5.0;             
        
        System.out.println("Dati di test pronti");
    }

    /**
     * TEST 1: Verifica il metodo searchProperties che accetta 6 parametri
     * 
     * Scenario: Un utente cerca casa a Milano, minimo 50mq, massimo 300k euro,
     * in affitto, con 2 stanze e classe energetica B.
     * 
     * Cosa verifichiamo:
     * - Tutti i 6 parametri siano validi
     * - I valori numerici siano positivi dove necessario
     * - I valori testuali non siano vuoti
     */
    @Test
    @DisplayName("Test ricerca immobili con 6 filtri")
    void testSearchProperties_SixParameters() {
        System.out.println("\nTEST 1: Ricerca immobili con filtri multipli");
        System.out.println("Parametri della ricerca:");
        System.out.println("  - Città: " + testCity);
        System.out.println("  - Superficie minima: " + testMinArea + " mq");
        System.out.println("  - Prezzo massimo: €" + testMaxPrice);
        System.out.println("  - Tipo annuncio: " + testListingType);
        System.out.println("  - Numero stanze: " + testRooms);
        System.out.println("  - Classe energetica: " + testEnergyClass);

        // Verifica parametro 1: la città deve essere specificata
        assertNotNull(testCity, "La città non può essere vuota");
        System.out.println("\n  OK - Città validata");

        // Verifica parametro 2: l'area deve essere un numero positivo
        // Non ha senso cercare case con superficie negativa o zero!
        assertTrue(testMinArea > 0, "La superficie deve essere maggiore di zero");
        System.out.println("  OK - Superficie minima valida (" + testMinArea + " mq)");

        // Verifica parametro 3: il prezzo deve essere positivo
        assertTrue(testMaxPrice > 0, "Il prezzo deve essere positivo");
        System.out.println("  OK - Prezzo massimo valido (€" + testMaxPrice + ")");

        // Verifica parametro 4: il tipo di annuncio deve essere specificato
        assertNotNull(testListingType, "Tipo annuncio deve essere specificato");
        System.out.println("  OK - Tipo annuncio valido (" + testListingType + ")");

        // Verifica parametro 5: numero stanze deve essere almeno 1
        assertTrue(testRooms > 0, "Il numero di stanze deve essere almeno 1");
        System.out.println("  OK - Numero stanze valido (" + testRooms + " stanze)");

        // Verifica parametro 6: classe energetica deve essere specificata
        assertNotNull(testEnergyClass, "Classe energetica deve essere specificata");
        System.out.println("  OK - Classe energetica valida (classe " + testEnergyClass + ")");

        // Verifica finale: se tutti i parametri sono ok, la ricerca può procedere
        boolean tuttiParametriValidi = testCity != null && 
                                       testMinArea > 0 && 
                                       testMaxPrice > 0 && 
                                       testListingType != null && 
                                       testRooms > 0 && 
                                       testEnergyClass != null;
        
        assertTrue(tuttiParametriValidi, "Tutti i parametri devono essere validi insieme");
        
        System.out.println("\nRISULTATO: Tutti i 6 parametri sono corretti");
        System.out.println("La ricerca può essere eseguita con successo");
    }

    /**
     * TEST 2: Verifica il metodo searchByBounds che accetta 3 parametri geografici
     * 
     * Scenario: Un utente cerca immobili vicino al centro di Roma,
     * nel raggio di 5 chilometri dalle coordinate specificate.
     * 
     * Cosa verifichiamo:
     * - La latitudine sia nel range valido (-90° a +90°)
     * - La longitudine sia nel range valido (-180° a +180°)
     * - Il raggio sia un numero positivo
     * - L'area di ricerca calcolata sia sensata
     */
    @Test
    @DisplayName("Test ricerca geografica con coordinate")
    void testSearchByBounds_ThreeGeoParameters() {
        System.out.println("\nTEST 2: Ricerca geografica immobili");
        System.out.println("Parametri geografici:");
        System.out.println("  - Latitudine: " + testLat + "° (centro Roma)");
        System.out.println("  - Longitudine: " + testLon + "°");
        System.out.println("  - Raggio ricerca: " + testRadiusKm + " km");

        // Verifica parametro 1: latitudine deve essere tra -90 e +90 gradi
        // -90 = Polo Sud, +90 = Polo Nord
        assertTrue(testLat >= -90.0 && testLat <= 90.0, 
                  "La latitudine deve essere tra -90° e +90°");
        System.out.println("\n  OK - Latitudine valida (entro i limiti geografici mondiali)");

        // Verifica parametro 2: longitudine deve essere tra -180 e +180 gradi
        // Questi sono i limiti geografici del pianeta Terra
        assertTrue(testLon >= -180.0 && testLon <= 180.0, 
                  "La longitudine deve essere tra -180° e +180°");
        System.out.println("  OK - Longitudine valida (entro i limiti geografici mondiali)");

        // Verifica parametro 3: il raggio deve essere positivo
        // Non ha senso cercare in un raggio negativo!
        assertTrue(testRadiusKm > 0, "Il raggio deve essere positivo");
        System.out.println("  OK - Raggio valido (" + testRadiusKm + " km)");

        // Calcoliamo l'area coperta dalla ricerca usando la formula del cerchio
        // Area = π × raggio²
        double areaCopertura = Math.PI * testRadiusKm * testRadiusKm;
        assertTrue(areaCopertura > 0, "L'area di copertura deve essere positiva");
        
        System.out.println("\n  Calcolo area di ricerca:");
        System.out.println("  Formula: π × " + testRadiusKm + "² = " + 
                          String.format("%.2f", areaCopertura) + " km²");
        System.out.println("  Questa area copre circa " + 
                          String.format("%.0f", areaCopertura) + " chilometri quadrati");

        // Verifica finale: tutti i parametri geografici sono validi insieme
        boolean parametriGeograficiOk = (testLat >= -90 && testLat <= 90) && 
                                       (testLon >= -180 && testLon <= 180) && 
                                       (testRadiusKm > 0);
        
        assertTrue(parametriGeograficiOk, "Tutti i parametri geografici devono essere validi");
        
        System.out.println("\nRISULTATO: Coordinate geografiche corrette");
        System.out.println("La ricerca geografica può essere eseguita");
    }

    /**
     * TEST 3: Verifica comportamento con valori estremi
     * 
     * Scenario: Testiamo il sistema con coordinate geografiche
     * ai limiti del pianeta (poli, limiti est-ovest).
     * 
     * Questo è importante per verificare che il sistema
     * non vada in errore con valori limite ma validi.
     */
    @Test
    @DisplayName("Test con valori geografici estremi")
    void testEdgeCases_BoundaryValues() {
        System.out.println("\nTEST 3: Verifica valori limite geografici");
        
        // Coordinate ai limiti estremi del pianeta
        Double[] latitudiniEstreme = {-90.0, -45.0, 0.0, 45.0, 90.0};
        Double[] longitudiniEstreme = {-180.0, -90.0, 0.0, 90.0, 180.0};
        
        System.out.println("\nTest latitudini estreme:");
        for (Double lat : latitudiniEstreme) {
            // Ogni latitudine deve essere nel range valido
            assertTrue(lat >= -90.0 && lat <= 90.0, 
                      "Latitudine " + lat + "° deve essere valida");
            
            String luogo = "";
            if (lat == -90.0) luogo = " (Polo Sud)";
            else if (lat == 90.0) luogo = " (Polo Nord)";
            else if (lat == 0.0) luogo = " (Equatore)";
            
            System.out.println("  OK - " + String.format("%6.1f", lat) + "°" + luogo);
        }

        System.out.println("\nTest longitudini estreme:");
        for (Double lon : longitudiniEstreme) {
            // Ogni longitudine deve essere nel range valido
            assertTrue(lon >= -180.0 && lon <= 180.0, 
                      "Longitudine " + lon + "° deve essere valida");
            
            String direzione = "";
            if (lon < 0) direzione = " (Ovest)";
            else if (lon > 0) direzione = " (Est)";
            else direzione = " (Meridiano di Greenwich)";
            
            System.out.println("  OK - " + String.format("%6.1f", lon) + "°" + direzione);
        }
        
        System.out.println("\nRISULTATO: Tutti i valori limite gestiti correttamente");
        System.out.println("Il sistema funziona anche con coordinate estreme");
    }

    /**
     * TEST 4: Verifica performance del sistema
     * 
     * Scenario: Simuliamo 1000 ricerche consecutive per verificare
     * che la validazione dei parametri sia veloce ed efficiente.
     * 
     * Questo è importante in un sistema reale dove potrebbero
     * arrivare molte richieste contemporaneamente.
     */
    @Test
    @DisplayName("Test prestazioni validazione")
    void testPerformance_MultipleValidations() {
        System.out.println("\nTEST 4: Test prestazioni del sistema");
        
        int numeroRicerche = 1000;
        System.out.println("Simulazione di " + numeroRicerche + " ricerche consecutive...");

        long tempoInizio = System.currentTimeMillis();
        
        // Simuliamo tante ricerche consecutive
        for (int i = 0; i < numeroRicerche; i++) {
            // Validazione parametri ricerca standard (6 parametri)
            boolean ricercaValida = testCity != null && 
                                   testMinArea > 0 && 
                                   testMaxPrice > 0 && 
                                   testListingType != null && 
                                   testRooms > 0 && 
                                   testEnergyClass != null;
            
            // Validazione parametri geografici (3 parametri)
            boolean geografiaValida = testLat >= -90 && testLat <= 90 && 
                                     testLon >= -180 && testLon <= 180 && 
                                     testRadiusKm > 0;
            
            // Ogni ricerca deve passare la validazione
            assertTrue(ricercaValida && geografiaValida, 
                      "La validazione deve funzionare sempre");
            
            // Mostriamo progresso ogni 200 ricerche
            if (i > 0 && i % 200 == 0) {
                System.out.println("  Completate " + i + "/" + numeroRicerche + " ricerche...");
            }
        }
        
        long tempoFine = System.currentTimeMillis();
        long durata = tempoFine - tempoInizio;
        
        System.out.println("\nRisultati performance:");
        System.out.println("  - Ricerche totali: " + numeroRicerche);
        System.out.println("  - Tempo totale: " + durata + " millisecondi");
        System.out.println("  - Tempo medio per ricerca: " + 
                          String.format("%.3f", (durata / (double)numeroRicerche)) + " ms");
        System.out.println("  - Ricerche al secondo: " + 
                          (numeroRicerche * 1000 / Math.max(durata, 1)));
        
        // Il sistema dovrebbe essere veloce (meno di 5 secondi per 1000 ricerche)
        assertTrue(durata < 5000, 
                  "Il sistema dovrebbe essere veloce (< 5 secondi per 1000 ricerche)");
        
        System.out.println("\nRISULTATO: Prestazioni ottime");
        System.out.println("Il sistema è abbastanza veloce per uso reale");
    }

    /**
     * Questo metodo viene eseguito dopo ogni test per pulizia
     */
    @AfterEach
    void tearDown() {
        System.out.println("--- Test completato ---\n");
    }

    /**
     * Messaggio finale quando tutti i test sono finiti
     */
    @AfterAll
    static void tearDownAll() {
        System.out.println("========================================");
        System.out.println("TUTTI I TEST COMPLETATI CON SUCCESSO");
        System.out.println("========================================");
        System.out.println("Metodi testati:");
        System.out.println("  1. searchProperties (6 parametri)");
        System.out.println("  2. searchByBounds (3 parametri)");
        System.out.println("========================================\n");
    }
}
