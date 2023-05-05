package com.funixproductions.core.crud.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public abstract class ApiDTO implements Serializable, RequestDTO {
    private UUID id;
    private Date createdAt;
    private Date updatedAt;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof final ApiDTO apiDTO) {
            if (this.id != null && apiDTO.getId() != null) {
                return apiDTO.getId().equals(this.id);
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
