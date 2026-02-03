package com.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "user_id")
    private Long userId;

    private Double amount;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public Bid(Long propertyId, Long userId, Double amount) {
        this.propertyId = propertyId;
        this.userId = userId;
        this.amount = amount;
        this.publishedAt = LocalDateTime.now();
    }
}
