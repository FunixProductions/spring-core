package com.funixproductions.core.files.doc.repositories;

import com.funixproductions.core.crud.repositories.ApiRepository;
import com.funixproductions.core.files.doc.entities.TestStorageFile;
import org.springframework.stereotype.Repository;

@Repository
public interface TestStorageRepository extends ApiRepository<TestStorageFile> {
}
