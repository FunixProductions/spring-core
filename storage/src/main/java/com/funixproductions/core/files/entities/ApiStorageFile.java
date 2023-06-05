package com.funixproductions.core.files.entities;

import com.funixproductions.core.crud.entities.ApiEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ApiStorageFile extends ApiEntity {

    @Column(nullable = false, name = "file_name", unique = true)
    private String fileName;

    @Column(nullable = false, name = "file_path", unique = true)
    private String filePath;

    @Column(nullable = false, name = "file_size")
    private Long fileSize;

    @Column(nullable = false, name = "file_extension")
    private String fileExtension;

}
