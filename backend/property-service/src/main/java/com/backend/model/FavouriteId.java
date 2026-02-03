package com.backend.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavouriteId implements Serializable {
    @jakarta.persistence.Column(name = "id_user")
    private Long userId;

    @jakarta.persistence.Column(name = "id_prop")
    private Long propertyId;

    public FavouriteId() {
    }

    public FavouriteId(Long userId, Long propertyId) {
        this.userId = userId;
        this.propertyId = propertyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FavouriteId that = (FavouriteId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(propertyId, that.propertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, propertyId);
    }
}
