package com.dietiestates.user_service.dto;

import org.springframework.http.ResponseCookie;

public record TokenPair(String access, ResponseCookie refreshCookie) {
}
