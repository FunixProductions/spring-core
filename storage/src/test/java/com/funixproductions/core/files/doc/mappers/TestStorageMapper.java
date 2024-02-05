package com.funixproductions.core.files.doc.mappers;


import com.funixproductions.core.crud.mappers.ApiMapper;
import com.funixproductions.core.files.doc.dtos.TestStorageFileDTO;
import com.funixproductions.core.files.doc.entities.TestStorageFile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestStorageMapper extends ApiMapper<TestStorageFile, TestStorageFileDTO> {
}
