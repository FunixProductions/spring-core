package com.funixproductions.core.crud.doc.clients;

import com.funixproductions.core.crud.clients.CrudClient;
import com.funixproductions.core.crud.doc.dtos.TestDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("test")
public interface TestClient extends CrudClient<TestDTO> {
}
