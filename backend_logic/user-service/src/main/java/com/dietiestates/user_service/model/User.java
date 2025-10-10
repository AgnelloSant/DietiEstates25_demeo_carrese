package com.dietiestates.user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String email;
    public String password;
    public String phone;
    public String role;

    @Column(name = "google_id")
    private String googleId;
    
    @Column(name = "facebook_id")
    private String facebookId;
    
    @Column(name = "provider")
    private String provider; 


    @Column(name = "github_id")
    private String githubId;


    //costruttori


    public User() {
    }

    public User(Long id, String name, String email, String password, String phone, String role, String googleId,
            String facebookId, String provider,String githubId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.googleId = googleId;
        this.facebookId = facebookId;
        this.provider = provider;
        this.githubId=githubId;
    }
    



    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", phone=" + phone
                + ", role=" + role + ", googleId=" + googleId + ", facebookId=" + facebookId + ", provider=" + provider
                + ", githubId=" + githubId + ", getId()=" + getId() + ", getName()=" + getName() + ", getEmail()="
                + getEmail() + ", getPassword()=" + getPassword() + ", getPhone()=" + getPhone() + ", getRole()="
                + getRole() + ", getGoogleId()=" + getGoogleId() + ", getFacebookId()=" + getFacebookId()
                + ", getProvider()=" + getProvider() + ", getClass()=" + getClass() + ", getGithubId()=" + getGithubId()
                + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }



    

    //getter setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }



    
}