package com.dietiestates.user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavouriteId implements Serializable {
    @Column(name = "id_user")
    private Long userId;
    @Column(name = "id_prop")
    private Long propId;

    public FavouriteId() {}
    public FavouriteId(Long userId, Long propId) {
        this.userId = userId;
        this.propId = propId;
    }

    public Long getUserId() { return userId; }
    public Long getPropId() { return propId; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setPropId(Long propId) { this.propId = propId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavouriteId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(propId, that.propId);
    }
    @Override public int hashCode() { return Objects.hash(userId, propId); }
}
