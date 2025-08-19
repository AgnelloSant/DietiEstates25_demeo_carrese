package com.dietiestates.user_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.dietiestates.user_service.dto.RegisterRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class RegistrationLogic {

    @Autowired
    private UserRepository userRepository;
    private JavaMailSender mailSender; //modificare (o creare) mail in application.properties


    public boolean userRegister(RegisterRequest registerRequest) {

        Optional<User> userOpt = userRepository.findByEmail(registerRequest.getEmail());
      

        if (userOpt.isPresent()) {
            return false; // L'email è già registrata
        }

        // Controllo formato email
        String email = registerRequest.getEmail();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; //sono consentiti caratteri alfabetici Maiuscoli e minuscoli,
                                                                // numeri e singoli caratteri speciali +, _, - e . prima (e dopo) della @
        if (!email.matches(emailRegex)) {
            return false; // Email non valida
        }

        confirmPsw(registerRequest); 

        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(registerRequest.getPassword());
        newUser.setPhone(registerRequest.getPhone());
        newUser.setRole(registerRequest.getRole());

        newUser = userRepository.save(newUser); 
        if(newUser.getId() != null) {

            System.out.println("Registrazione avvenuta con successo || ID: " + newUser.getId());
            System.out.println("Invio email di conferma a: " + newUser.getEmail());

            sendConfirmationEmail(newUser.getEmail());
            return true; 

        }else {

            System.out.println("Errore durante la registrazione dell'utente");

            return false; 
        }

    }

    private void sendConfirmationEmail(String email) {
        try{

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Conferma registrazione");
            helper.setText("*****************BENVENUTO IN DIETIESTATES***************** \n\n\n\n\n Grazie per esserti registrato!");
            
            mailSender.send(message);

        }catch(MessagingException e) {
            e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println("Errore durante l'invio dell'email di conferma");
        }
    }

    public boolean confirmPsw(RegisterRequest registerRequest){ 
        String psw = registerRequest.getPassword(); 
        String pswRegex = "^(?=.*[!@#$%^&*()_+-=[]{};':|,.<>?])(?=.*[a-zA-Z0-9]).{8,}$"; 

        if(!psw.matches(pswRegex)){
            return false; //la password deve avere almeno 8 caratteri di cui uno speciale
        }
        return true;
    }
}
