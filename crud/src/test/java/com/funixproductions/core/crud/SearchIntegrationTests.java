package com.funixproductions.core.crud;

import com.funixproductions.core.TestApp;
import com.funixproductions.core.crud.doc.dtos.TestDTO;
import com.funixproductions.core.crud.doc.entities.TestEntity;
import com.funixproductions.core.crud.doc.enums.TestEnum;
import com.funixproductions.core.crud.doc.mappers.TestMapper;
import com.funixproductions.core.crud.doc.repositories.TestRepository;
import com.funixproductions.core.crud.doc.repositories.TestSubRepository;
import com.funixproductions.core.crud.doc.services.TestService;
import com.funixproductions.core.crud.dtos.PageDTO;
import com.funixproductions.core.crud.enums.SearchOperation;
import com.funixproductions.core.test.beans.JsonHelper;
import com.funixproductions.core.tools.time.TimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        classes = {
                TestApp.class
        }
)
class SearchIntegrationTests {

    static final String ROUTE = "/test";

    private final String dateString = "06-09-2022_18.00.00";

    @Autowired
    private TestRepository repository;

    @Autowired
    private TestSubRepository testSubRepository;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonHelper gson;

    @Autowired
    private TestService testService;

    @BeforeEach
    void cleanDb() {
        testSubRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void testSearchNoPagination() throws Exception {
        TestEntity testDTO = new TestEntity();
        testDTO.setData("ouiData");
        testDTO.setNumber(10);

        TestEntity testDTO1 = new TestEntity();
        testDTO1.setData("NonData");
        testDTO1.setNumber(11);

        this.repository.save(testDTO);
        this.repository.save(testDTO1);

        checkSearchSuccess(testMapper.toDto(testDTO), "data:" + SearchOperation.EQUALS.getOperation() + ":" + testDTO.getData());
        checkSearchSuccess(testMapper.toDto(testDTO), "number:" + SearchOperation.EQUALS.getOperation() + ":" + testDTO.getNumber());
        checkSearchSuccess(testMapper.toDto(testDTO1), "data:" + SearchOperation.EQUALS.getOperation() + ":" + testDTO1.getData());
        checkSearchSuccess(testMapper.toDto(testDTO1), "number:" + SearchOperation.EQUALS.getOperation() + ":" + testDTO1.getNumber());
        checkSearchSuccess(testMapper.toDto(testDTO1), "number:" + SearchOperation.GREATER_THAN_OR_EQUAL_TO.getOperation() + ":11");
        checkSearchSuccess(testMapper.toDto(testDTO), "number:" + SearchOperation.LESS_THAN_OR_EQUAL_TO.getOperation() + ":10");
        checkSearchSuccess(testMapper.toDto(testDTO), "number:" + SearchOperation.LESS_THAN_OR_EQUAL_TO.getOperation() + ":10,data:" + SearchOperation.EQUALS.getOperation() + ":ouiData");
    }

    @Test
    void testSearchMultiple() throws Exception {
        final List<TestEntity> testEntities = new ArrayList<>();

        testEntities.add(new TestEntity("ouiData", 10, Date.from(Instant.now()), null, null, null, null));
        testEntities.add(new TestEntity("NonData", 11, null, null, null, null, null));
        testEntities.add(new TestEntity("NonData", -1, null, null, null, null, null));

        this.repository.saveAllAndFlush(testEntities);

        checkSearchMultiple(2, String.format("number:%s:0", SearchOperation.GREATER_THAN.getOperation()));
        checkSearchMultiple(1, String.format("number:%s:5", SearchOperation.LESS_THAN.getOperation()));
        checkSearchMultiple(2, String.format("data:%s:NonData", SearchOperation.EQUALS.getOperation()));
        checkSearchMultiple(0, String.format("data:%s:NonData2", SearchOperation.EQUALS.getOperation()));
    }

    @Test
    void testSpacialOperations() throws Exception {
        final Instant now = TimeUtils.getTimeFromFrenchZone("dd-MM-yyyy_HH.mm.ss", dateString);
        final List<TestEntity> testEntities = new ArrayList<>();

        testEntities.add(new TestEntity("ouiData", 10, Date.from(now.minusSeconds(100)), 1.f, 10.0, true, TestEnum.ONE));
        testEntities.add(new TestEntity("NonData", 11, Date.from(now.plusSeconds(60)), 2.f, 5.0, true, TestEnum.TWO));
        testEntities.add(new TestEntity("NonData", -1, Date.from(now.minusSeconds(60)), -10.f, null, false, TestEnum.THREE));

        this.repository.saveAllAndFlush(testEntities);

        PageDTO<TestDTO> response = this.testService.getAll("", "", String.format("data:%s:NonData", SearchOperation.NOT_EQUALS.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals("ouiData", response.getContent().get(0).getData());

        response = this.testService.getAll("", "", String.format("data:%s:Non", SearchOperation.LIKE.getOperation()), "");
        assertEquals(2, response.getTotalElementsThisPage());
        assertEquals("NonData", response.getContent().get(0).getData());
        assertEquals("NonData", response.getContent().get(1).getData());

        response = this.testService.getAll("", "", String.format("data:%s:Non", SearchOperation.NOT_LIKE.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals("ouiData", response.getContent().get(0).getData());

        response = this.testService.getAll("", "", String.format("number:%s:10", SearchOperation.GREATER_THAN.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals("NonData", response.getContent().get(0).getData());
        assertEquals(11, response.getContent().get(0).getNumber());

        response = this.testService.getAll("", "", String.format("number:%s:10", SearchOperation.LESS_THAN.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals("NonData", response.getContent().get(0).getData());
        assertEquals(-1, response.getContent().get(0).getNumber());

        response = this.testService.getAll("", "", String.format("number:%s:10", SearchOperation.GREATER_THAN_OR_EQUAL_TO.getOperation()), "number:asc");
        assertEquals(2, response.getTotalElementsThisPage());
        assertEquals("ouiData", response.getContent().get(0).getData());
        assertEquals("NonData", response.getContent().get(1).getData());

        response = this.testService.getAll("", "", String.format("aDouble:%s:10", SearchOperation.IS_NULL.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals("NonData", response.getContent().get(0).getData());
        assertEquals(-1, response.getContent().get(0).getNumber());

        response = this.testService.getAll("", "", String.format("aDouble:%s:4", SearchOperation.GREATER_THAN.getOperation()), "aDouble:desc");
        assertEquals(2, response.getTotalElementsThisPage());
        assertEquals(10.0, response.getContent().get(0).getADouble());
        assertEquals(5.0, response.getContent().get(1).getADouble());

        response = this.testService.getAll("", "", String.format("data:%s:o", SearchOperation.IS_NOT_NULL.getOperation()), "");
        assertEquals(3, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("aBoolean:%s:o", SearchOperation.IS_TRUE.getOperation()), "");
        assertEquals(2, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("aBoolean:%s:o", SearchOperation.IS_FALSE.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("testEnum:%s:%s", SearchOperation.EQUALS.getOperation(), TestEnum.THREE), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals(TestEnum.THREE, response.getContent().get(0).getTestEnum());
        assertEquals(-1, response.getContent().get(0).getNumber());
    }

    @Test
    void testBracketsSearch() {
        final Instant now = TimeUtils.getTimeFromFrenchZone("dd-MM-yyyy_HH.mm.ss", dateString);
        final List<TestEntity> testEntities = new ArrayList<>();

        testEntities.add(new TestEntity("ouiData2", 10, Date.from(now.minusSeconds(100)), 1.f, 10.0, true, TestEnum.ONE));
        testEntities.add(new TestEntity("NonData2", 11, Date.from(now.plusSeconds(60)), 2.f, 5.0, true, TestEnum.TWO));
        testEntities.add(new TestEntity("dd", 11, Date.from(now.plusSeconds(60)), 2.f, 5.0, true, TestEnum.THREE));

        this.repository.saveAllAndFlush(testEntities);

        PageDTO<TestDTO> response = this.testService.getAll("", "", String.format("data:%s:[ouiData2]", SearchOperation.EQUALS.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("data:%s:[ouiData2|NonData2]", SearchOperation.EQUALS.getOperation()), "");
        assertEquals(2, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("data:%s:[ouiData2|NonData3]", SearchOperation.EQUALS.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("data:%s:[ouiData2|NonData2],number:%s:10", SearchOperation.EQUALS.getOperation(), SearchOperation.EQUALS.getOperation()), "");
        assertEquals(1, response.getTotalElementsThisPage());

        // put the comparatives after the bracket search, otherwise it will not work, the brackets will override the number oprand here
        response = this.testService.getAll("", "", String.format("number:%s:10,data:%s:[ouiData2|NonData2]", SearchOperation.EQUALS.getOperation(), SearchOperation.EQUALS.getOperation()), "");
        assertEquals(2, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("testEnum:%s:[TWO|THREE]", SearchOperation.EQUALS.getOperation()), "");
        assertEquals(2, response.getTotalElementsThisPage());
        for (final TestDTO testDTO : response.getContent()) {
            assertTrue(testDTO.getTestEnum() == TestEnum.TWO || testDTO.getTestEnum() == TestEnum.THREE);
        }
    }

    @Test
    void testFetchDates() throws Exception {
        final Instant now = Instant.now();
        final List<TestEntity> testEntities = new ArrayList<>();

        testEntities.add(new TestEntity("ouiData2", 10, Date.from(now.plusSeconds(100)), 1.f, 10.0, true, TestEnum.ONE));
        testEntities.add(new TestEntity("NonData2", 11, Date.from(now.plusSeconds(60)), 2.f, 5.0, true, TestEnum.TWO));
        testEntities.add(new TestEntity("dd", 11, Date.from(now.plusSeconds(60)), 2.f, 5.0, true, TestEnum.THREE));
        testEntities.add(new TestEntity("dd", 11, Date.from(now.minusSeconds(60)), 2.f, 5.0, true, TestEnum.THREE));

        this.repository.saveAllAndFlush(testEntities);

        PageDTO<TestDTO> response = this.testService.getAll("", "", String.format("date:%s:%s", SearchOperation.GREATER_THAN.getOperation(), now), "");
        assertEquals(3, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("date:%s:%s", SearchOperation.LESS_THAN.getOperation(), now), "");
        assertEquals(1, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("date:%s:%s", SearchOperation.GREATER_THAN.getOperation(), now.plusSeconds(70)), "");
        assertEquals(1, response.getTotalElementsThisPage());
    }

    @Test
    void testSearchStringFormattedUUID() throws Exception {
        TestDTO testDTO = new TestDTO();
        testDTO.setData(UUID.randomUUID().toString());
        testDTO = this.testService.create(testDTO);

        PageDTO<TestDTO> response = this.testService.getAll("", "", String.format("data:%s:%s", SearchOperation.EQUALS.getOperation(), testDTO.getData()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals(testDTO.getData(), response.getContent().get(0).getData());

        TestDTO testDTO2 = new TestDTO();
        testDTO2.setData(UUID.randomUUID().toString());
        testDTO2 = this.testService.create(testDTO2);

        response = this.testService.getAll("", "", String.format("data:%s:%s", SearchOperation.EQUALS.getOperation(), testDTO2.getData()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals(testDTO2.getData(), response.getContent().get(0).getData());
    }

    @Test
    void testSearchWithBracketsEmptyList() {
        assertDoesNotThrow(() -> this.testService.getAll("", "", String.format("data:%s:[],number:%s:10", SearchOperation.EQUALS.getOperation(), SearchOperation.EQUALS.getOperation()), ""));
    }

    @Test
    void testSearchEqualsIgnoreCase() {
        final String textToSearch = "ouiData" + UUID.randomUUID();

        TestDTO testDTO = new TestDTO();
        testDTO.setData(textToSearch);
        this.testService.create(testDTO);

        PageDTO<TestDTO> response = this.testService.getAll("", "", String.format("data:%s:%s", SearchOperation.EQUALS_IGNORE_CASE.getOperation(), textToSearch.toLowerCase()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals(textToSearch, response.getContent().get(0).getData());
    }

    @Test
    void testSearchLikeIgnoreCase() {
        final String textToSearch = "ouiData" + UUID.randomUUID();

        TestDTO testDTO = new TestDTO();
        testDTO.setData(textToSearch);
        this.testService.create(testDTO);

        PageDTO<TestDTO> response = this.testService.getAll("", "", String.format("data:%s:%s", SearchOperation.LIKE_IGNORE_CASE.getOperation(), textToSearch.toLowerCase()), "");
        assertEquals(1, response.getTotalElementsThisPage());
        assertEquals(textToSearch, response.getContent().get(0).getData());
    }

    @Test
    void testSearchErrorString() throws Exception {
        checkSearchFail("data-ouiData");
        checkSearchFail("oui:");
        checkSearchFail(":data");
        checkSearchFail("sdkjqsdhfqkjdshqgfksjdhfgkqsjdhfgkjsdqhfgkqsjdhfgsqkjdhfgksqjdhgf");
        checkSearchFail("data:slkdj:oui");
        checkSearchFail("data:slkdj:oui,oui:");
        checkSearchFail("data:slkdj:oui,oui");
        checkSearchFail("data:slkdj:oui,oui:u:a");
        checkSearchFail("data:" + SearchOperation.EQUALS);
        checkSearchFail("data:" + SearchOperation.EQUALS + ":oui,oui:d");
        checkSearchFail("proutDDDDDDD:" + SearchOperation.EQUALS + ":oui");
        checkSearchFail("number:" + SearchOperation.EQUALS_IGNORE_CASE + ":10");
        checkSearchFail("number:" + SearchOperation.LIKE_IGNORE_CASE + ":10");
    }

    @Test
    void testSearchDateWithIsNullOrIsNotNull() throws Exception {
        final Instant now = Instant.now();
        final List<TestEntity> testEntities = new ArrayList<>();

        testEntities.add(new TestEntity("ouiData2", 10, Date.from(now.plusSeconds(100)), 1.f, 10.0, true, TestEnum.ONE));
        testEntities.add(new TestEntity("NonData2", 11, Date.from(now.plusSeconds(60)), 2.f, 5.0, true, TestEnum.TWO));
        testEntities.add(new TestEntity("dd", 11, Date.from(now.plusSeconds(60)), 2.f, 5.0, true, TestEnum.THREE));
        testEntities.add(new TestEntity("dd", 11, Date.from(now.minusSeconds(60)), 2.f, 5.0, true, TestEnum.THREE));
        testEntities.add(new TestEntity("dd", 11, null, 2.f, 5.0, true, TestEnum.THREE));
        testEntities.add(new TestEntity("dd", 11, null, 2.f, 5.0, true, TestEnum.THREE));

        this.repository.saveAllAndFlush(testEntities);

        PageDTO<TestDTO> response = this.testService.getAll("", "", String.format("date:%s:null", SearchOperation.IS_NULL.getOperation()), "");
        assertEquals(2, response.getTotalElementsThisPage());

        response = this.testService.getAll("", "", String.format("date:%s:null", SearchOperation.IS_NOT_NULL.getOperation()), "");
        assertEquals(4, response.getTotalElementsThisPage());

    }

    private void checkSearchSuccess(final TestDTO toCheck, final String search) throws Exception {
        mockMvc.perform(get(ROUTE + "?search=" + search)).andExpect(status().isOk());
        final PageDTO<TestDTO> list = testService.getAll(null, null, search, null);

        assertEquals(1, list.getTotalElementsThisPage());
        final TestDTO check = list.getContent().get(0);
        assertNotNull(check.getId());
        assertNotNull(check.getCreatedAt());
        assertEquals(toCheck.getData(), check.getData());
        assertEquals(toCheck.getNumber(), check.getNumber());
    }

    private void checkSearchMultiple(final int nbrToGet, final String search) throws Exception {
        mockMvc.perform(get(ROUTE + "?search=" + search)).andExpect(status().isOk());

        final PageDTO<TestDTO> list = testService.getAll(null, null, search, null);
        assertEquals(nbrToGet, list.getTotalElementsThisPage());
    }

    private void checkSearchFail(final String search) throws Exception {
        mockMvc.perform(get(ROUTE + "?search=" + search)).andExpect(status().isBadRequest());
    }

}
