package com.funixproductions.core.files.dtos;

import com.funixproductions.core.crud.dtos.ApiDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ApiStorageFileDTO extends ApiDTO {

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileExtension;

}
