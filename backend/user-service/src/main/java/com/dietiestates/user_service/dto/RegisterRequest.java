package com.dietiestates.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class RegisterRequest {

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    public String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
    @Pattern(regexp = ".*[@#$%^&+=!\\-_/].*", message = "Password must contain at least one special character")
    public String password;

    public String phone;
    public String role;

    public RegisterRequest() {

    }

    public RegisterRequest(String name, String email, String password, String phone, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
