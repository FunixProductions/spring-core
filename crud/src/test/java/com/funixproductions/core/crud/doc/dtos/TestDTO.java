package com.funixproductions.core.crud.doc.dtos;

import com.funixproductions.core.crud.doc.enums.TestEnum;
import com.funixproductions.core.crud.dtos.ApiDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO extends ApiDTO {
    private String data;
    private Integer number;
    private Date date;
    private Float aFloat;
    private Double aDouble;
    private Boolean aBoolean;
    private TestEnum testEnum;
}
