package com.dietiestates.user_service;

import com.dietiestates.user_service.controller.AuthController;
import com.dietiestates.user_service.service.AuthService;
import com.dietiestates.user_service.config.HeaderAuthenticationFilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private HeaderAuthenticationFilter headerAuthenticationFilter;

    // ===============================
    // TC1 - EMAIL NON VALIDA
    // ===============================

    @Test
    @DisplayName("TC1 - Email non valida → 400")
    void tc1_invalidEmail() throws Exception {

        String json = """
                {
                    "email": "email_non_valida",
                    "password": "Password1@"
                }
                """;

        System.out.println("\n==============================");
        System.out.println("AVVIO TC1 - EMAIL NON VALIDA");
        System.out.println("JSON INVIATO:");
        System.out.println(json);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println("STATUS RICEVUTO: " + result.getResponse().getStatus());
        System.out.println("BODY RISPOSTA:");
        System.out.println(result.getResponse().getContentAsString());

        System.out.println("TC1 SUPERATO ");
        System.out.println("==============================\n");
    }

    // ===============================
    // TC2 - PASSWORD TROPPO CORTA
    // ===============================

    @Test
    @DisplayName("TC2 - Password troppo corta → 400")
    void tc2_shortPassword() throws Exception {

        String json = """
                {
                    "email": "test@mail.com",
                    "password": "Pass1@"
                }
                """;

        System.out.println("\n==============================");
        System.out.println("AVVIO TC2 - PASSWORD TROPPO CORTA");
        System.out.println("JSON INVIATO:");
        System.out.println(json);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println("STATUS RICEVUTO: " + result.getResponse().getStatus());
        System.out.println("BODY RISPOSTA:");
        System.out.println(result.getResponse().getContentAsString());

        System.out.println("TC2 SUPERATO ");
        System.out.println("==============================\n");
    }

    // ===============================
    // TC3 - CREDENZIALI VALIDE MA UTENTE NON TROVATO
    // ===============================

    @Test
    @DisplayName("TC3 - Credenziali valide ma utente non trovato → 401")
    void tc3_validButUnauthorized() throws Exception {

        when(authService.userLogin(any(), any(), any()))
                .thenReturn(Optional.empty());

        String json = """
                {
                    "email": "test@mail.com",
                    "password": "Password1@"
                }
                """;

        System.out.println("\n==============================");
        System.out.println("AVVIO TC3 - UTENTE NON TROVATO");
        System.out.println("JSON INVIATO:");
        System.out.println(json);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();

        System.out.println("STATUS RICEVUTO: " + result.getResponse().getStatus());
        System.out.println("BODY RISPOSTA:");
        System.out.println(result.getResponse().getContentAsString());

        System.out.println("TC3 SUPERATO ");
        System.out.println("==============================\n");
    }
}
