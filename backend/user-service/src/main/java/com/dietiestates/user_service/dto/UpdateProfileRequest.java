package com.dietiestates.user_service.dto;

public record UpdateProfileRequest(
    String name,
    String phone
) {}