package com.funixproductions.core.files.clients;

import com.funixproductions.core.crud.clients.CrudClient;
import com.funixproductions.core.files.dtos.ApiStorageFileDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * FeignClient(name = "name", url = "${funix.api.app-domain-url}", path = "/path")
 * @param <DTO> dto
 */
public interface StorageCrudClient<DTO extends ApiStorageFileDTO> extends CrudClient<DTO> {

    @PostMapping("/file")
    DTO store(@RequestParam("file") MultipartFile file);

    @GetMapping("/file/{id}")
    Resource loadAsResource(@PathVariable("id") String id);

}
