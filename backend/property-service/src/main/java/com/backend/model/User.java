package com.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phoneNumber;
}