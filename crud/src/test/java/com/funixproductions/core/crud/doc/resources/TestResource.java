package com.funixproductions.core.crud.doc.resources;

import com.funixproductions.core.crud.doc.clients.TestClient;
import com.funixproductions.core.crud.doc.dtos.TestDTO;
import com.funixproductions.core.crud.doc.services.TestService;
import com.funixproductions.core.crud.resources.ApiResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestResource extends ApiResource<TestDTO, TestService> implements TestClient {
    public TestResource(TestService service) {
        super(service);
    }
}
