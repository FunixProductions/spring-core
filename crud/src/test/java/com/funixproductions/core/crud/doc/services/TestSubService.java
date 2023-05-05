package com.funixproductions.core.crud.doc.services;

import com.funixproductions.core.crud.doc.dtos.TestSubDTO;
import com.funixproductions.core.crud.doc.entities.TestEntity;
import com.funixproductions.core.crud.doc.entities.TestSubEntity;
import com.funixproductions.core.crud.doc.mappers.TestSubMapper;
import com.funixproductions.core.crud.doc.repositories.TestRepository;
import com.funixproductions.core.crud.doc.repositories.TestSubRepository;
import com.funixproductions.core.crud.services.ApiService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestSubService extends ApiService<TestSubDTO, TestSubEntity, TestSubMapper, TestSubRepository> {

    private final TestRepository testRepository;

    public TestSubService(TestSubRepository repository,
                          TestRepository testRepository,
                          TestSubMapper mapper) {
        super(repository, mapper);
        this.testRepository = testRepository;
    }

    @Override
    public void beforeSavingEntity(@NonNull Iterable<TestSubEntity> entity) {
        List<String> uuids = new ArrayList<>();

        for (TestSubEntity testSubEntity : entity) {
            if (testSubEntity.getMain() != null && testSubEntity.getMain().getUuid() != null) {
                uuids.add(testSubEntity.getMain().getUuid().toString());
            }
        }

        Iterable<TestEntity> foundEntities = this.testRepository.findAllByUuidIn(uuids);

        Map<String, TestEntity> entityMap = new HashMap<>();
        for (TestEntity entitySearch : foundEntities) {
            entityMap.put(entitySearch.getUuid().toString(), entitySearch);
        }

        for (TestSubEntity testSubEntity : entity) {
            if (testSubEntity.getMain() != null && testSubEntity.getMain().getUuid() != null) {
                TestEntity testEntity = entityMap.get(testSubEntity.getMain().getUuid().toString());

                if (testEntity != null) {
                    testSubEntity.setMain(testEntity);
                }
            }
        }
    }
}
