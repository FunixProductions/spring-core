package com.funixproductions.core.crud.resources;

import com.funixproductions.core.crud.clients.CrudClient;
import com.funixproductions.core.crud.dtos.ApiDTO;
import com.funixproductions.core.crud.dtos.PageDTO;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class ApiResource<DTO extends ApiDTO, SERVICE extends CrudClient<DTO>> implements CrudClient<DTO> {

    private final SERVICE service;

    @Override
    public PageDTO<DTO> getAll(String page, String elemsPerPage, String search, String sort) {
        return service.getAll(page, elemsPerPage, search, sort);
    }

    @Override
    public DTO findById(String id) {
        return service.findById(id);
    }

    @Override
    public DTO create(DTO request) {
        return service.create(request);
    }

    @Override
    public List<DTO> create(@Valid List<@Valid DTO> request) {
        return service.create(request);
    }

    @Override
    public DTO update(DTO request) {
        return service.update(request);
    }

    @Override
    public List<DTO> update(List<DTO> request) {
        return service.update(request);
    }

    @Override
    public DTO updatePut(DTO request) {
        return service.updatePut(request);
    }

    @Override
    public List<DTO> updatePut(@Valid List<@Valid DTO> request) {
        return service.updatePut(request);
    }

    @Override
    public void delete(String id) {
        service.delete(id);
    }

    @Override
    public void delete(String... ids) {
        service.delete(ids);
    }
}
