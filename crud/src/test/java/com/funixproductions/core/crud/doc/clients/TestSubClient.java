package com.funixproductions.core.crud.doc.clients;

import com.funixproductions.core.crud.clients.CrudClient;
import com.funixproductions.core.crud.doc.dtos.TestSubDTO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("TestSub")
public interface TestSubClient extends CrudClient<TestSubDTO> {
}
