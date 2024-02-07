package com.funixproductions.core.files;

import com.funixproductions.core.TestApp;
import com.funixproductions.core.files.doc.dtos.TestStorageFileDTO;
import com.funixproductions.core.test.beans.JsonHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        classes = {
                TestApp.class
        }
)
class ApiStorageResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonHelper jsonHelper;

    @Test
    void testUploadSuccess() throws Exception {
        final String fileName = "fileNameTest" + UUID.randomUUID();
        final String fileExt = "txt";
        final String fileContent = "test";
        final TestStorageFileDTO request = new TestStorageFileDTO();
        final MockMultipartFile file = new MockMultipartFile("file", fileName + "." + fileExt, "text/plain", fileContent.getBytes());

        request.setData(UUID.randomUUID().toString());
        final MockMultipartFile metadata = new MockMultipartFile(
                "dto",
                "dto",
                MediaType.APPLICATION_JSON_VALUE,
                jsonHelper.toJson(request).getBytes(StandardCharsets.UTF_8));

        MvcResult result = this.mockMvc.perform(multipart("/testfile/file")
                        .file(file)
                        .file(metadata)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final TestStorageFileDTO storageFileDTO = jsonHelper.fromJson(result.getResponse().getContentAsString(), TestStorageFileDTO.class);
        assertEquals(fileName + '.' + fileExt, storageFileDTO.getFileName());
        assertEquals(request.getData(), storageFileDTO.getData());
        assertEquals(fileExt, storageFileDTO.getFileExtension());

        result = this.mockMvc.perform(get("/testfile/file/" + storageFileDTO.getId()))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(fileContent, result.getResponse().getContentAsString());

        this.mockMvc.perform(get("/testfile/file/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFile() throws Exception {
        final String fileName = "fileNameTest" + UUID.randomUUID();
        final String fileExt = "txt";
        final String fileContent = "test";
        final TestStorageFileDTO request = new TestStorageFileDTO();
        final MockMultipartFile file = new MockMultipartFile("file", fileName + "." + fileExt, "text/plain", fileContent.getBytes());

        request.setData(UUID.randomUUID().toString());
        final MockMultipartFile metadata = new MockMultipartFile(
                "dto",
                "dto",
                MediaType.APPLICATION_JSON_VALUE,
                jsonHelper.toJson(request).getBytes(StandardCharsets.UTF_8));

        MvcResult result = this.mockMvc.perform(multipart("/testfile/file")
                        .file(file)
                        .file(metadata)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final TestStorageFileDTO storageFileDTO = jsonHelper.fromJson(result.getResponse().getContentAsString(), TestStorageFileDTO.class);
        assertEquals(fileName + '.' + fileExt, storageFileDTO.getFileName());
        assertEquals(fileExt, storageFileDTO.getFileExtension());

        this.mockMvc.perform(delete("/testfile?id=" + storageFileDTO.getId()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/testfile/file/" + storageFileDTO.getId()))
                .andExpect(status().isNotFound());
    }

}
