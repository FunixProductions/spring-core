package com.funixproductions.core.crud.doc.mappers;

import com.funixproductions.core.crud.doc.dtos.TestSubDTO;
import com.funixproductions.core.crud.doc.entities.TestSubEntity;
import com.funixproductions.core.crud.mappers.ApiMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = TestMapper.class)
public interface TestSubMapper extends ApiMapper<TestSubEntity, TestSubDTO> {
}
