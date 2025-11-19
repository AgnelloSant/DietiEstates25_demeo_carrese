package com.dietiestates.user_service.rep;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dietiestates.user_service.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

}
