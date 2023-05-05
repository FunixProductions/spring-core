package com.funixproductions.core.crud.doc.repositories;

import com.funixproductions.core.crud.doc.entities.TestEntity;
import com.funixproductions.core.crud.repositories.ApiRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends ApiRepository<TestEntity> {
}
