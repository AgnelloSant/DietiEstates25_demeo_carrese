package com.dietiestates.user_service.rep;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dietiestates.user_service.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    List<User> findAll();
    void update(User user);
    void deleteById(String id); 

    Optional<User> findByEmail(String email);
    
}