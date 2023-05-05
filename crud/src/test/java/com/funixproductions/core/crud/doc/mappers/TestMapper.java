package com.funixproductions.core.crud.doc.mappers;

import com.funixproductions.core.crud.doc.dtos.TestDTO;
import com.funixproductions.core.crud.doc.entities.TestEntity;
import com.funixproductions.core.crud.mappers.ApiMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TestMapper extends ApiMapper<TestEntity, TestDTO> {
    @Override
    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    TestEntity toEntity(TestDTO dto);

    @Override
    @Mapping(target = "id", source = "uuid")
    TestDTO toDto(TestEntity entity);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(TestEntity request, @MappingTarget TestEntity toPatch);
}
