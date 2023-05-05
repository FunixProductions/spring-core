package com.funixproductions.core.crud.doc.services;

import com.funixproductions.core.crud.doc.dtos.TestDTO;
import com.funixproductions.core.crud.doc.entities.TestEntity;
import com.funixproductions.core.crud.doc.mappers.TestMapper;
import com.funixproductions.core.crud.doc.repositories.TestRepository;
import com.funixproductions.core.crud.services.ApiService;
import org.springframework.stereotype.Service;

@Service
public class TestService extends ApiService<TestDTO, TestEntity, TestMapper, TestRepository> {

    public TestService(TestRepository repository,
                       TestMapper mapper) {
        super(repository, mapper);
    }

}
