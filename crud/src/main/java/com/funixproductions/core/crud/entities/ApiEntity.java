package com.funixproductions.core.crud.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class ApiEntity implements Serializable {
    /**
     * <p>Unique id (private not on DTO)</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    /**
     * <p>Unique uuid public</p>
     */
    @NaturalId
    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private String uuid;

    /**
     * <p>Creation date entity</p>
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    /**
     * <p>Last updated entity date</p>
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * <p>Method called by JPA in creation</p>
     */
    @PrePersist
    public void onCreate() {
        updateUuid();
        createdAt = new Date();
    }

    /**
     * <p>Method called on last update</p>
     */
    @PreUpdate
    public void onUpdate() {
        updateUuid();
        updatedAt = new Date();
    }

    private void updateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    /**
     * @return UUID unique uuid
     */
    public UUID getUuid() {
        if (uuid == null) {
            return null;
        } else {
            return UUID.fromString(uuid);
        }
    }

    /**
     * @param uuid unique UUID
     */
    public void setUuid(final UUID uuid) {
        if (uuid == null) {
            this.uuid = null;
        } else {
            this.uuid = uuid.toString();
        }
    }

    /**
     * @param obj object to compare
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof final ApiEntity apiEntity) {
            if (this.id != null && apiEntity.getId() != null) {
                return apiEntity.getId().equals(this.id);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }
}
