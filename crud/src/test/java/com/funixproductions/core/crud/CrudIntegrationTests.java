package com.funixproductions.core.crud;

import com.funixproductions.core.TestApp;
import com.funixproductions.core.crud.doc.dtos.TestDTO;
import com.funixproductions.core.crud.doc.entities.TestEntity;
import com.funixproductions.core.crud.doc.enums.TestEnum;
import com.funixproductions.core.crud.doc.repositories.TestRepository;
import com.funixproductions.core.crud.doc.services.TestService;
import com.funixproductions.core.crud.dtos.PageDTO;
import com.funixproductions.core.test.beans.JsonHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        classes = {
                TestApp.class
        }
)
class CrudIntegrationTests {
    public static final String ROUTE = "/test";

    private final JsonHelper gson;
    private final MockMvc mockMvc;
    private final TestRepository repository;
    private final TestService service;

    @Autowired
    public CrudIntegrationTests(MockMvc mockMvc,
                                TestRepository repository,
                                JsonHelper gson,
                                TestService service) {
        this.mockMvc = mockMvc;
        this.repository = repository;
        this.gson = gson;
        this.service = service;
    }

    @BeforeEach
    void cleanDB() {
        repository.deleteAll();
    }

    @Test
    void testCreation() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");

        MvcResult mvcResult = mockMvc.perform(post(ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(testDTO)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO result = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertEquals(testDTO.getData(), result.getData());
    }

    @Test
    void testUpdate() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");

        MvcResult mvcResult = mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO created = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        created.setData("changed");
        created.setCreatedAt(null);

        mvcResult = mockMvc.perform(patch(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(created)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO result = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        assertNotNull(result.getCreatedAt());
        assertNotEquals(created.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getData(), result.getData());
    }

    @Test
    void testCreateBatch() throws Exception {
        final List<TestDTO> list = new ArrayList<>();

        TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");
        list.add(testDTO);

        testDTO = new TestDTO();
        testDTO.setData("oui2");
        list.add(testDTO);

        mockMvc.perform(post(ROUTE + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(list)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateBlankFields() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");

        MvcResult mvcResult = mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO created = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        created.setData("    ");
        created.setCreatedAt(null);

        mvcResult = mockMvc.perform(patch(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(created)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO result = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        assertNotNull(result.getCreatedAt());
        assertNotEquals(created.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(created.getId(), result.getId());
        assertEquals(testDTO.getData(), result.getData());
    }

    @Test
    void testUpdateBatch() throws Exception {
        final List<TestDTO> list = new ArrayList<>();

        TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");
        MvcResult mvcResult = mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isOk())
                .andReturn();
        TestDTO created = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        list.add(created);

        testDTO = new TestDTO();
        testDTO.setData("oui2");
        mvcResult = mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isOk())
                .andReturn();
        created = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        list.add(created);

        int i = 0;
        for (final TestDTO test : list) {
            test.setData("oui " + i);
        }

        mvcResult = mockMvc.perform(patch(ROUTE + "/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(list)))
                .andExpect(status().isOk())
                .andReturn();

        final Type type = new com.google.common.reflect.TypeToken<List<TestDTO>>(){}.getType();
        final List<TestDTO> result = gson.fromJson(mvcResult.getResponse().getContentAsString(), type);

        for (final TestDTO dto : result) {
            TestDTO compare = null;

            for (final TestDTO search : list) {
                if (search.equals(dto)) {
                    compare = search;
                }
            }

            if (compare != null) {
                assertNotNull(dto.getCreatedAt());
                assertNotEquals(compare.getUpdatedAt(), dto.getUpdatedAt());
                assertEquals(compare.getId(), dto.getId());
                assertEquals(compare.getData(), dto.getData());
            } else {
                fail("elem not found");
            }
        }
    }

    @Test
    void testUpdateNoId() throws Exception {
        mockMvc.perform(patch(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(new TestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutNoId() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");

        mockMvc.perform(put(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutNoData() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setId(UUID.randomUUID());

        mockMvc.perform(put(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPut() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");
        testDTO.setNumber(1);
        testDTO.setDate(new Date());
        testDTO.setAFloat(1.0f);
        testDTO.setADouble(1.0);
        testDTO.setABoolean(true);
        testDTO.setTestEnum(TestEnum.ONE);

        MvcResult mvcResult = mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO created = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        final TestDTO toPut = new TestDTO();
        toPut.setData("changed");
        toPut.setId(created.getId());

        mvcResult = mockMvc.perform(put(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(toPut)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO result = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        assertEquals(created.getCreatedAt(), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(toPut.getData(), result.getData());
        assertNull(result.getNumber());
        assertNull(result.getDate());
        assertNull(result.getAFloat());
        assertNull(result.getADouble());
        assertNull(result.getABoolean());
        assertNull(result.getTestEnum());
    }

    @Test
    void testUpdateEntityNotCreated() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setId(UUID.randomUUID());

        mockMvc.perform(patch(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        int size = 3;

        for (int i = 0; i < size; ++i) {
            final TestEntity entity = new TestEntity();

            entity.setData(Integer.toString(i));
            this.repository.save(entity);
        }

        mockMvc.perform(get(ROUTE)).andExpect(status().isOk());

        final PageDTO<TestDTO> entities = service.getAll(null, null, null, null);

        assertEquals(size, entities.getTotalElementsThisPage());
        for (final TestDTO entity : entities.getContent()) {
            assertNotNull(entity.getData());
            assertNotNull(entity.getId());
        }
    }

    @Test
    void testGetAllPaginated() throws Exception {
        int size = 3;

        for (int i = 0; i < size; ++i) {
            final TestEntity entity = new TestEntity();

            entity.setData(Integer.toString(i));
            this.repository.save(entity);
        }

        mockMvc.perform(get(ROUTE + "?page=0&elemsPerPage=1")).andExpect(status().isOk());

        PageDTO<TestDTO> entities = service.getAll("0", "1", null, null);
        assertEquals(1, entities.getTotalElementsThisPage());

        mockMvc.perform(get(ROUTE + "?page=0&elemsPerPage=2")).andExpect(status().isOk());

        entities = service.getAll("0", "2", null, null);
        assertEquals(2, entities.getTotalElementsThisPage());
    }

    @Test
    void testGetAllPaginatedAndSorted() throws Exception {
        final int startInt = 1000;
        final int minimalInt = -4000;

        for (int i = 0; i < 3; ++i) {
            final TestEntity entity = new TestEntity();

            entity.setData(Integer.toString(i));
            entity.setNumber(startInt + i);
            this.repository.save(entity);
        }

        this.repository.save(new TestEntity("test", minimalInt, null, null, null, null, null));

        mockMvc.perform(get(ROUTE + "?page=0&elemsPerPage=1&sort=number:desc")).andExpect(status().isOk());
        PageDTO<TestDTO> entities = service.getAll("0", "1", null, "number:desc");
        assertEquals(1, entities.getTotalElementsThisPage());
        assertEquals(startInt + 2, entities.getContent().get(0).getNumber());

        mockMvc.perform(get(ROUTE + "?page=1&elemsPerPage=1&sort=number:desc")).andExpect(status().isOk());
        entities = service.getAll("1", "1", null, "number:desc");
        assertEquals(1, entities.getTotalElementsThisPage());
        assertEquals(startInt + 1, entities.getContent().get(0).getNumber());

        mockMvc.perform(get(ROUTE + "?page=2&elemsPerPage=1&sort=number:desc")).andExpect(status().isOk());
        entities = service.getAll("2", "1", null, "number:desc");
        assertEquals(1, entities.getTotalElementsThisPage());
        assertEquals(startInt, entities.getContent().get(0).getNumber());

        mockMvc.perform(get(ROUTE + "?page=0&elemsPerPage=3&sort=number:desc")).andExpect(status().isOk());
        entities = service.getAll("0", "3", null, "number:desc");
        assertEquals(3, entities.getTotalElementsThisPage());
        assertEquals(startInt + 2, entities.getContent().get(0).getNumber());
        assertEquals(startInt + 1, entities.getContent().get(1).getNumber());
        assertEquals(startInt, entities.getContent().get(2).getNumber());

        mockMvc.perform(get(ROUTE + "?page=0&elemsPerPage=1&sort=number:asc")).andExpect(status().isOk());
        entities = service.getAll("0", "1", null, "number:asc");
        assertEquals(1, entities.getTotalElementsThisPage());
        assertEquals(minimalInt, entities.getContent().get(0).getNumber());
    }

    @Test
    void testGetById() throws Exception {
        final TestDTO testDTO = new TestDTO();
        testDTO.setData("oui");

        MvcResult mvcResult = mockMvc.perform(post(ROUTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(testDTO)))
                .andExpect(status().isOk())
                .andReturn();

        final TestDTO result = gson.fromJson(mvcResult.getResponse().getContentAsString(), TestDTO.class);
        mockMvc.perform(get(ROUTE + "/" + result.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByIdNotCreated() throws Exception {
        mockMvc.perform(get(ROUTE + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemove() throws Exception {
        TestEntity entity = new TestEntity();

        entity.setData("TEST");
        entity = this.repository.save(entity);

        List<TestDTO> entities = service.getAll(null, null, null, null).getContent();
        assertEquals(1, entities.size());

        mockMvc.perform(delete(ROUTE + "?id=" + entity.getUuid())).andExpect(status().isOk());

        entities = service.getAll(null, null, null, null).getContent();
        assertEquals(0, entities.size());
    }

    @Test
    void testRemoveNoId() throws Exception {
        mockMvc.perform(delete(ROUTE))
                .andExpect(status().isBadRequest());
    }

}
