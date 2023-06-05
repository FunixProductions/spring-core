package com.funixproductions.core.files.doc.mappers;


import com.funixproductions.core.files.doc.dtos.TestStorageFileDTO;
import com.funixproductions.core.files.doc.entities.TestStorageFile;
import com.funixproductions.core.files.mappers.ApiStorageMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestStorageMapper extends ApiStorageMapper<TestStorageFile, TestStorageFileDTO> {
}
