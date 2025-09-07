package com.dietiestates.user_service.dto;

public record LoginResponse(
    String accessToken,
    PublicUserDTO user
) {
    
}


