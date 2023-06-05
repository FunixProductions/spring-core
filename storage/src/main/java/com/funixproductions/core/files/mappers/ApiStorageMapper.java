package com.funixproductions.core.files.mappers;

import com.funixproductions.core.crud.mappers.ApiMapper;
import com.funixproductions.core.files.dtos.ApiStorageFileDTO;
import com.funixproductions.core.files.entities.ApiStorageFile;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

@MapperConfig
public interface ApiStorageMapper<ENTITY extends ApiStorageFile, DTO extends ApiStorageFileDTO> extends ApiMapper<ENTITY, DTO> {

    @Mapping(target = "fileName", source = "originalFilename")
    @Mapping(target = "fileSize", source = "size")
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "fileExtension", ignore = true)
    DTO toDto(MultipartFile multipartFile);

}
