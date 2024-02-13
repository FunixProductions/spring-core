package com.funixproductions.core.files.ressources;

import com.funixproductions.core.crud.resources.ApiResource;
import com.funixproductions.core.files.clients.StorageCrudClient;
import com.funixproductions.core.files.dtos.ApiStorageFileDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public abstract class ApiStorageResource<DTO extends ApiStorageFileDTO, SERVICE extends StorageCrudClient<DTO>> extends ApiResource<DTO, SERVICE> implements StorageCrudClient<DTO> {

    protected ApiStorageResource(final SERVICE service) {
        super(service);
    }

    @Override
    public DTO store(final DTO dto, MultipartFile file) {
        return super.getService().store(dto, file);
    }

    @Override
    public DTO updatePartial(DTO dto, MultipartFile file) {
        return super.getService().updatePartial(dto, file);
    }

    @Override
    public DTO updateFull(DTO dto, MultipartFile file) {
        return super.getService().updateFull(dto, file);
    }

    @Override
    public Resource loadAsResource(String id) {
        return super.getService().loadAsResource(id);
    }
}
