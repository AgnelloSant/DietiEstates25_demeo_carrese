package com.dietiestates.user_service.dto;

import com.dietiestates.user_service.model.User;

public record AuthResult(User user, TokenPair tokens) {
}
