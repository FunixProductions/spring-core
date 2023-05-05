package com.funixproductions.core.crud.doc.entities;

import com.funixproductions.core.crud.doc.enums.TestEnum;
import com.funixproductions.core.crud.entities.ApiEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TestEntity extends ApiEntity {
    private String data;
    private Integer number;
    private Date date;
    private Float aFloat;
    private Double aDouble;
    private Boolean aBoolean;
    private TestEnum testEnum;
}
