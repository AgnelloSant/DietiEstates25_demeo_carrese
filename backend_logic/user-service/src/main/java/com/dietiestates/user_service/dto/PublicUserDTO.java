package com.dietiestates.user_service.dto;

public record PublicUserDTO(
    Long id,
    String email,
    String role
) {
    
}