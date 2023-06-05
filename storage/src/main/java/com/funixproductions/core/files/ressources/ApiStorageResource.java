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
    public DTO store(MultipartFile file) {
        return super.getService().store(file);
    }

    @Override
    public Resource loadAsResource(String id) {
        return super.getService().loadAsResource(id);
    }

    public void beforeStoringFile(MultipartFile multipartFile) {}
    public void beforeLoadingResource(String id) {}
}
