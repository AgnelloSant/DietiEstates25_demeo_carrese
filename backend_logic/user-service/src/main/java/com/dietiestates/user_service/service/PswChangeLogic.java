package com.dietiestates.user_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dietiestates.user_service.dto.PswChangeRequest;
import com.dietiestates.user_service.model.User;
import com.dietiestates.user_service.rep.UserRepository;

@Service
public class PswChangeLogic {
    
    @Autowired
    private UserRepository userRepository;

    public boolean userPswChange(PswChangeRequest pswChangeRequest) {
        try {
             String email = pswChangeRequest.getEmail(); 
             Optional<User> userOpt = userRepository.findByEmail(email);
            if(confirmPsw(pswChangeRequest) == false){ 
                throw new Exception("Password must have a special character (!@#$%^&*()_+-=[]{};':|,.<>?) and be at least 8 digits long.  "); 
                 
            }
            if(userOpt.isEmpty()){ 
                throw new Exception("User not found"); 
            }else{
                User user = userOpt.get();
                if(user.getPassword().equals(pswChangeRequest.getOldPsw())){ 
                    user.setPassword(pswChangeRequest.getNewPsw()); 
                    userRepository.save(user);
                }else{ 
                    throw new Exception("Wrong Password");
                }
                return true ;
            }
        }catch (Exception e) {
            System.out.println("Error during the request " + e.getMessage());
            return false; 
        }
       
    }

    public boolean confirmPsw(PswChangeRequest pswChangeRequest){ 
        String psw = pswChangeRequest.getNewPsw(); 
        String pswRegex = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=.*[a-zA-Z0-9]).{8,}$";


        if(!psw.matches(pswRegex)){
            return false; //la password deve avere almeno 8 caratteri di cui uno speciale
        }else{
            return true; //password valida
        }
    }
}
