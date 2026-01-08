package com.dietiestates;

import com.dietiestates.user_service.auth.AuthService;
import com.dietiestates.user_service.controller.UserController;
import com.dietiestates.user_service.dto.*;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.service.*;
import com.dietiestates.shared.security.JwtProperties;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
// Import statici fondamentali
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private LoginLogic loginLogic;
    @Mock private FavouritesLogic favouritesLogic;
    @Mock private HttpServletRequest httpReq;
    
    // Mock accessori per il costruttore
    @Mock private RegistrationLogic registerLogic;
    @Mock private PswChangeLogic pswChangeLogic;
    @Mock private AuthService authService;
    @Mock private JwtProperties jwtProps;
    @Mock private UpdateProfileLogic updateProfileLogic;

    @InjectMocks
    private UserController userController;

    // ===================================================================================
    // TEST 1: LOGIN
    // ===================================================================================
    @ParameterizedTest(name = "EP Login {index}: Email={0}, Pass={1}, Outcome={2} -> Status={3}")
    @CsvSource({
        "mario@email.com, pass123, SUCCESS, 200",
        "mario@email.com, wrongpw, FAILURE, 401",
        "null, pass123, FAILURE, 401",
        "mario@email.com, null, FAILURE, 401"
    })
    void testLogin(
            String email, String password, String outcome, Integer expectedStatus) {

        // SETUP
        String actualEmail = "null".equals(email) ? null : email;
        String actualPass = "null".equals(password) ? null : password;
        LoginRequest req = new LoginRequest(actualEmail, actualPass);

        lenient().when(httpReq.getHeader("User-Agent")).thenReturn("TestAgent");
        lenient().when(httpReq.getRemoteAddr()).thenReturn("127.0.0.1");

        // MOCKING qui istruiamo il test per cosa deve rispondere per sostituire api e db
        if ("SUCCESS".equals(outcome)) {
            User dummyUser = new User();
            dummyUser.setId(1L);
            dummyUser.setEmail(actualEmail);
            dummyUser.setRole("USER");

            ResponseCookie dummyCookie = ResponseCookie.from("refresh", "cookieVal").build();
            AuthService.Tokens dummyTokens = new AuthService.Tokens("fake-access-token", dummyCookie);

            LoginLogic.LoginResult dummyResult = new LoginLogic.LoginResult(dummyUser, dummyTokens);

            when(loginLogic.userLogin(any(), anyString(), anyString()))
                    .thenReturn(Optional.of(dummyResult));
        } else {
            lenient().when(loginLogic.userLogin(any(), anyString(), anyString()))
                    .thenReturn(Optional.empty());
        }
        //qui chiamiamo il controller vero 
        // EXECUTION
        ResponseEntity<LoginResponse> response = userController.login(req, httpReq);

        // ASSERTION
        assertThat(response.getStatusCode().value(), is(expectedStatus));

        if (expectedStatus == 200) {
            assertThat(response.getBody(), notNullValue());
            assertThat(response.getBody().accessToken(), is("fake-access-token"));
            
            //controlliamo che nel body ci sia l emai giusta
            assertThat(response.getBody().user().email(), is(actualEmail));
            

            String cookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            assertThat(cookieHeader, notNullValue());
            assertThat(cookieHeader, containsString("refresh=cookieVal"));
        }
    }

    // ===================================================================================
    // TEST 2: ADD FAVOURITE
    // ===================================================================================
    @ParameterizedTest(name = "BVA AddFav {index}: User={0}, Prop={1} -> {2}")
    @CsvSource({
        "100, 50, VALID",
        "100, 1, VALID",
        "100, 2, VALID",
        "100, 9223372036854775806, VALID",
        "100, 9223372036854775807, VALID",
        "100, 0, INVALID",
        "100, -1, INVALID",
        "1, 50, VALID",
        "2, 50, VALID",
        "9223372036854775806, 50, VALID",
        "9223372036854775807, 50, VALID",
        "abc, 50, INVALID_FORMAT",
        "null, 50, INVALID_FORMAT"
    })
    void testAddFavourite_Robustness(String userId, Long propId, String expected) {

        String actualUserId = "null".equals(userId) ? null : userId;

       
        FavouriteListRequest favReq = new FavouriteListRequest(0L, propId);

        if ("VALID".equals(expected)) {
            ResponseEntity<Boolean> response = userController.addFavourite(actualUserId, favReq);

            assertThat(response.getStatusCode().value(), is(200));
            assertThat(response.getBody(), is(true));

            // FONDAMENTALE: Verifichiamo che il controller abbia convertito la Stringa in Long
    // e abbia passato la palla al service Logic
            verify(favouritesLogic).addFavouriteProperty(Long.valueOf(actualUserId), propId);

        } else if ("INVALID".equals(expected)) {
            userController.addFavourite(actualUserId, favReq);
            // VERIFICA: Anche se l'ID è brutto (-1), il controller DEVE chiamare il service.
    // (Sarà poi il service a decidere se esplodere o no, ma qui testiamo il controller)
            verify(favouritesLogic).addFavouriteProperty(Long.valueOf(actualUserId), propId);

        } else {
            assertThrows(NumberFormatException.class, () -> {
                userController.addFavourite(actualUserId, favReq);
            });
            verifyNoInteractions(favouritesLogic);
        }
    }
}