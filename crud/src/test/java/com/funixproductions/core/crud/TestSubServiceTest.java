package com.funixproductions.core.crud;

import com.funixproductions.core.TestApp;
import com.funixproductions.core.TestApp;
import com.funixproductions.core.crud.doc.dtos.TestDTO;
import com.funixproductions.core.crud.doc.dtos.TestSubDTO;
import com.funixproductions.core.crud.doc.enums.TestEnum;
import com.funixproductions.core.crud.doc.services.TestService;
import com.funixproductions.core.crud.doc.services.TestSubService;
import com.funixproductions.core.crud.dtos.PageDTO;
import com.funixproductions.core.crud.enums.SearchOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApp.class)
class TestSubServiceTest {

    @Autowired
    private TestSubService testSubService;

    @Autowired
    private TestService testService;

    @BeforeEach
    void resetDb() {
        testSubService.getRepository().deleteAll();
        testService.getRepository().deleteAll();
    }

    @Test
    void testCreationEntity() {
        final TestDTO testDTO = this.generateMain();
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO res = this.testSubService.create(subDTO);
            assertEquals(subDTO.getMain(), res.getMain());

            this.testSubService.create(new TestSubDTO());
            final TestSubDTO testNew = new TestSubDTO();
            testNew.setData("data");
            this.testSubService.create(testNew);
        });
    }

    @Test
    void testPatchEntity() {
        final TestDTO testDTO = this.generateMain();
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO res = this.testSubService.create(subDTO);
            res.setData("random");

            final TestSubDTO patched = this.testSubService.update(res);
            assertEquals(res.getData(), patched.getData());
        });
    }

    @Test
    void testSearchWithSubObject() {
        final TestDTO testDTO = this.generateMain();
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO created = this.testSubService.create(subDTO);
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.data:%s:%s", SearchOperation.EQUALS.getOperation(), testDTO.getData()),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(created.getId(), search.getContent().get(0).getId());
            assertEquals(testDTO.getData(), search.getContent().get(0).getMain().getData());
        });
    }

    @Test
    void testSearchNotEqualsSubObject() {
        final TestDTO testDTO = this.generateMain();
        final TestDTO testDTO2 = this.generateMain();
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO created = this.testSubService.create(subDTO);
            this.testSubService.create(new TestSubDTO(testDTO2, UUID.randomUUID().toString()));
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.data:%s:%s", SearchOperation.NOT_EQUALS.getOperation(), testDTO.getData()),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertNotEquals(created.getId(), search.getContent().get(0).getId());
            assertEquals(testDTO2.getData(), search.getContent().get(0).getMain().getData());
        });
    }

    @Test
    void testSearchGreaterThanSubObject() {
        final Random random = new Random();
        final int startnbr = 10;

        final TestDTO testDTO = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestDTO testDTO2 = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr + 1,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());
        final TestSubDTO subDTO2 = new TestSubDTO(testDTO2, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            this.testSubService.create(List.of(subDTO2, subDTO));
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.number:%s:%d", SearchOperation.GREATER_THAN.getOperation(), testDTO.getNumber()),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(testDTO2.getNumber(), search.getContent().get(0).getMain().getNumber());
        });
    }

    @Test
    void testSearchGreaterThanOrEqualsSubObject() {
        final Random random = new Random();
        final int startnbr = 10;

        final TestDTO testDTO = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestDTO testDTO2 = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr + 1,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());
        final TestSubDTO subDTO2 = new TestSubDTO(testDTO2, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            this.testSubService.create(List.of(subDTO2, subDTO));
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.number:%s:%d", SearchOperation.GREATER_THAN_OR_EQUAL_TO.getOperation(), testDTO.getNumber()),
                    "createdAt:desc"
            );

            assertEquals(2, search.getTotalElementsThisPage());
        });
    }

    @Test
    void testSearchLessThanSubObject() {
        final Random random = new Random();
        final int startnbr = 10;

        final TestDTO testDTO = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestDTO testDTO2 = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr + 1,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());
        final TestSubDTO subDTO2 = new TestSubDTO(testDTO2, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            this.testSubService.create(List.of(subDTO2, subDTO));
            PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.number:%s:%d", SearchOperation.LESS_THAN.getOperation(), testDTO.getNumber()),
                    "createdAt:desc"
            );
            assertEquals(0, search.getTotalElementsThisPage());

            testDTO.setNumber(testDTO.getNumber() - 1);
            this.testService.update(testDTO);
            search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.number:%s:%d", SearchOperation.LESS_THAN.getOperation(), testDTO.getNumber() + 1),
                    "createdAt:desc"
            );
            assertEquals(1, search.getTotalElementsThisPage());
        });
    }

    @Test
    void testSearchLessThanOrEqualsSubObject() {
        final Random random = new Random();
        final int startnbr = 10;

        final TestDTO testDTO = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestDTO testDTO2 = this.testService.create(new TestDTO(
                UUID.randomUUID().toString(),
                startnbr + 1,
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        ));
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());
        final TestSubDTO subDTO2 = new TestSubDTO(testDTO2, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            this.testSubService.create(List.of(subDTO2, subDTO));
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.number:%s:%d", SearchOperation.LESS_THAN_OR_EQUAL_TO.getOperation(), testDTO.getNumber()),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(testDTO.getNumber(), search.getContent().get(0).getMain().getNumber());
        });
    }

    @Test
    void testSearchLikeWithSubObject() {
        final TestDTO testDTO = this.generateMain();
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            testDTO.setData("Ouioui");
            final TestDTO patched = this.testService.update(testDTO);

            final TestSubDTO created = this.testSubService.create(subDTO);
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.data:%s:%s", SearchOperation.LIKE.getOperation(), "oui"),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(created.getId(), search.getContent().get(0).getId());
            assertEquals(patched.getData(), search.getContent().get(0).getMain().getData());
        });
    }

    @Test
    void testSearchNotLikeWithSubObject() {
        final TestDTO testDTO = this.generateMain();
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            testDTO.setData("Ouioui");
            final TestDTO patched = this.testService.update(testDTO);

            final TestSubDTO created = this.testSubService.create(subDTO);
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.data:%s:%s", SearchOperation.NOT_LIKE.getOperation(), "non"),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(created.getId(), search.getContent().get(0).getId());
            assertEquals(patched.getData(), search.getContent().get(0).getMain().getData());
        });
    }

    @Test
    void testSearchIsNullWithSubObject() {
        final TestDTO testDTO = this.testService.create(new TestDTO(
                null,
                10,
                new Date(),
                0.5f,
                0.5,
                true,
                TestEnum.ONE
        ));
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO created = this.testSubService.create(subDTO);
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.data:%s:%s", SearchOperation.IS_NULL.getOperation(), "yes"),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(created.getId(), search.getContent().get(0).getId());
            assertNull(search.getContent().get(0).getMain().getData());
        });
    }

    @Test
    void testSearchIsNotNullWithSubObject() {
        final TestDTO testDTO = this.generateMain();
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO created = this.testSubService.create(subDTO);
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.data:%s:%s", SearchOperation.IS_NOT_NULL.getOperation(), "non"),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(created.getId(), search.getContent().get(0).getId());
            assertEquals(testDTO.getData(), search.getContent().get(0).getMain().getData());
        });
    }

    @Test
    void testSearchIsTrueWithSubObject() {
        final TestDTO testDTO = this.testService.create(new TestDTO(
                null,
                10,
                new Date(),
                0.5f,
                0.5,
                true,
                TestEnum.ONE
        ));
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO created = this.testSubService.create(subDTO);
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.aBoolean:%s:%s", SearchOperation.IS_TRUE.getOperation(), "yes"),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(created.getId(), search.getContent().get(0).getId());
            assertTrue(search.getContent().get(0).getMain().getABoolean());
        });
    }

    @Test
    void testSearchIsFalseWithSubObject() {
        final TestDTO testDTO = this.testService.create(new TestDTO(
                null,
                10,
                new Date(),
                0.5f,
                0.5,
                false,
                TestEnum.ONE
        ));
        final TestSubDTO subDTO = new TestSubDTO(testDTO, UUID.randomUUID().toString());

        assertDoesNotThrow(() -> {
            final TestSubDTO created = this.testSubService.create(subDTO);
            final PageDTO<TestSubDTO> search = this.testSubService.getAll(
                    "0",
                    "5",
                    String.format("main.aBoolean:%s:%s", SearchOperation.IS_FALSE.getOperation(), "yes"),
                    "createdAt:desc"
            );

            assertEquals(1, search.getTotalElementsThisPage());
            assertEquals(created.getId(), search.getContent().get(0).getId());
            assertFalse(search.getContent().get(0).getMain().getABoolean());
        });
    }

    private TestDTO generateMain() {
        final Random random = new Random();

        final TestDTO testDTO = new TestDTO(
                UUID.randomUUID().toString(),
                random.nextInt(),
                new Date(),
                random.nextFloat(),
                random.nextDouble(),
                random.nextBoolean(),
                TestEnum.ONE
        );

        return this.testService.create(testDTO);
    }

}
