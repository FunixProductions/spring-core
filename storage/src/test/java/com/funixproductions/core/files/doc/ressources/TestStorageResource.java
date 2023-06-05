package com.funixproductions.core.files.doc.ressources;

import com.funixproductions.core.files.doc.dtos.TestStorageFileDTO;
import com.funixproductions.core.files.doc.services.TestStorageService;
import com.funixproductions.core.files.ressources.ApiStorageResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("testfile")
public class TestStorageResource extends ApiStorageResource<TestStorageFileDTO, TestStorageService> {

    public TestStorageResource(TestStorageService service) {
        super(service);
    }

}
