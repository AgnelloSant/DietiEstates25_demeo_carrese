package com.dietiestates.user_service.controller;

import org.springframework.http.HttpStatus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.dietiestates.user_service.dto.FavouriteListRequest;
import com.dietiestates.user_service.dto.LoginRequest;
import com.dietiestates.user_service.dto.LoginResponse;
import com.dietiestates.user_service.dto.RegisterRequest;
import com.dietiestates.user_service.service.LoginLogic;
import com.dietiestates.user_service.service.PswChangeLogic;
import com.dietiestates.user_service.service.RegistrationLogic;
import com.dietiestates.user_service.model.User;
import com.dietiestates.shared.dto.PropertySearchDTO;

import com.dietiestates.user_service.dto.PswChangeRequest;
import com.dietiestates.user_service.service.FavouritesLogic;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private LoginLogic loginLogic;

    @Autowired
    private RegistrationLogic registerLogic;

    @Autowired
    private PswChangeLogic pswChangeLogic;

    @Autowired
    private FavouritesLogic favouritesLogic;
    
  @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    User user = loginLogic.userLogin(loginRequest);

    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    LoginResponse response = new LoginResponse(
        String.valueOf(user.getId()),
        user.getName(),
        user.getEmail(),
        user.getPhone(),
        user.getRole()
    );

    return ResponseEntity.ok(response);
}

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        boolean success = registerLogic.userRegister(registerRequest);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed");
        }
        return ResponseEntity.ok("Registration successful");
    }

    @PutMapping("/newpsw")
    public ResponseEntity<String> newPsw(@RequestBody PswChangeRequest pswChangeRequest) {
        boolean success = pswChangeLogic.userPswChange(pswChangeRequest);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password change failed");
        }
        return ResponseEntity.ok("Password change successful");
    }

    @GetMapping("/favourites")
    public ResponseEntity<List<PropertySearchDTO>> getFavourites()
    {   
        System.out.println("UserController.getFavourites: ");
        System.err.println("UserController.getFavourites: userId=" );
        //DEVO IMPLEMENTARE LA LOGICA PER OTTENERE ID UTENTE.
        Long userId = 2L; // getCurrentUserId();
        //chiamo il servizio
        return ResponseEntity.ok(favouritesLogic.getFavouriteProperties(userId));
    }

    
    @PostMapping("/addfavourite")
    public ResponseEntity<Boolean> addFavourite(@RequestBody FavouriteListRequest favRequest)
    {
        Long userId = 2L; //favRequest.getIdUser();
        Long propId = 4L;//favRequest.getIdProp();

        favouritesLogic.addFavouriteProperty(userId, propId);
        return ResponseEntity.ok(true);

    } 
    
    @GetMapping("/ping")
public ResponseEntity<String> ping() {
  System.out.println(">>> PING NUOVO CODICE <<<");
  return ResponseEntity.ok("OK");
}


    //@PostMapping("/")
}
