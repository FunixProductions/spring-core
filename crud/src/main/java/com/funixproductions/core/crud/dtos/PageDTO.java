package com.funixproductions.core.crud.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T extends RequestDTO> {

    private List<T> content;

    private Integer totalPages;

    private Integer actualPage;

    private Long totalElementsDatabase;

    private Integer totalElementsThisPage;

    public PageDTO(final Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.actualPage = page.getNumber();
        this.totalElementsDatabase = page.getTotalElements();
        this.totalElementsThisPage = page.getNumberOfElements();
    }

}
