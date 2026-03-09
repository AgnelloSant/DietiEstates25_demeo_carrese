package com.backend;

import com.backend.controller.SearchController;
import com.backend.service.SearchService;
import com.backend.util.JwtUtil;
import com.backend.filter.JwtAuthenticationFilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // ===============================
    // TC1 - LAT INVALIDA
    // ===============================
    @Test
    @DisplayName("TC1 - lat < -90 → 400")
    void tc1_invalidLat() throws Exception {

        System.out.println("\nTC1 - LAT INVALIDA");

        mockMvc.perform(get("/search/bybounds")
                        .param("lat", "-100")
                        .param("lon", "10")
                        .param("radiusKm", "10"))
                .andExpect(status().isBadRequest());

        System.out.println("✔ TC1 PASS");
    }

    // ===============================
    // TC2 - LON INVALIDA
    // ===============================
    @Test
    @DisplayName("TC2 - lon > 180 → 400")
    void tc2_invalidLon() throws Exception {

        System.out.println("\nTC2 - LON INVALIDA");

        mockMvc.perform(get("/search/bybounds")
                        .param("lat", "45")
                        .param("lon", "200")
                        .param("radiusKm", "10"))
                .andExpect(status().isBadRequest());

        System.out.println("✔ TC2 PASS");
    }

    // ===============================
    // TC3 - TUTTO VALIDO
    // ===============================
    @Test
    @DisplayName("TC3 - Parametri validi → 200")
    void tc3_validInput() throws Exception {

        System.out.println("\nTC3 - PARAMETRI VALIDI");

        when(searchService.searchByBounds(45.0, 10.0, 10.0))
                .thenReturn(List.of());

        mockMvc.perform(get("/search/bybounds")
                        .param("lat", "45")
                        .param("lon", "10")
                        .param("radiusKm", "10"))
                .andExpect(status().isOk());

        System.out.println("✔ TC3 PASS");
    }
}
