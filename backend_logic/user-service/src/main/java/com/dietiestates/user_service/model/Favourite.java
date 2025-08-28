package com.dietiestates.user_service.model;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
  name = "favourites"
)
public class Favourite {

    @EmbeddedId
    private FavouriteId id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Favourite() {}
    public Favourite(FavouriteId id) { this.id = id; }
    public Favourite(FavouriteId id, Instant createdAt) { this.id = id; this.createdAt = createdAt; }

    public FavouriteId getId() { return id; }
    public void setId(FavouriteId id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
}
