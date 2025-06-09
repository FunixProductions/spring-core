package com.funixproductions.core.files.doc.services;

import com.funixproductions.core.files.doc.dtos.TestStorageFileDTO;
import com.funixproductions.core.files.doc.entities.TestStorageFile;
import com.funixproductions.core.files.doc.mappers.TestStorageMapper;
import com.funixproductions.core.files.doc.repositories.TestStorageRepository;
import com.funixproductions.core.files.services.ApiStorageService;
import org.springframework.stereotype.Service;

@Service
public class TestStorageService extends ApiStorageService<TestStorageFileDTO, TestStorageFile, TestStorageMapper, TestStorageRepository> {

    public TestStorageService(TestStorageMapper mapper, TestStorageRepository repository) {
        super("test-storage-service", null, repository, mapper);
    }

}
