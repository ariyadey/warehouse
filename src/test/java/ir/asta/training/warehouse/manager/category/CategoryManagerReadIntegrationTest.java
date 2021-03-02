package ir.asta.training.warehouse.manager.category;

import ir.asta.training.warehouse.dto.CategorySaveRequestDto;
import ir.asta.training.warehouse.dto.CategorySearchParamsDto;
import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.SortDirection;
import ir.asta.training.warehouse.manager.category.exception.CategoryIllegalSearchException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional(readOnly = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryManagerReadIntegrationTest {

    private final Map<String, String> categoryMap = new HashMap<>();

    @Autowired
    CategoryManager manager;

    @Autowired
    Environment env;

    @BeforeAll
    void populateDB() {
        final List<String> subjects = Collections.unmodifiableList(Arrays.asList(
                "Horror",
                "Fiction",
                "Biography",
                "History",
                "Humor",
                "Business",
                "Fantasy",
                "Historical Fiction",
                "Sports"
        ));

        for (String subject : subjects) {
            categoryMap.put(subject, manager.save(new CategorySaveRequestDto(subject)).getCode());
        }
    }

    @Test
    void Should_LoadCorrectEntity_When_SearchingWithExplicitCode() {
        CategorySearchParamsDto searchParams = CategorySearchParamsDto.builder()
                .subject("or")
                .code(categoryMap.get("History"))
                .pageSize(4)
                .pageNumber(1)
                .orderBy("subject")
                .sortDirection(SortDirection.DESC)
                .build();

        List<CategoryEntity> expectedEntities = Collections.singletonList(
                new CategoryEntity(categoryMap.get("History"), "History"));
        List<CategoryEntity> dbLoadedEntities = manager.search(searchParams);
        clearIds(dbLoadedEntities);

        assertEquals(expectedEntities, dbLoadedEntities);
    }

    @Test
    void Should_LoadCorrectEntities_When_SearchingWithExplicitSubject() {
        CategorySearchParamsDto searchParams = CategorySearchParamsDto.builder()
                .subject("or")
                .orderBy("subject")
                .pageSize(3)
                .pageNumber(2)
                .sortDirection(SortDirection.ASC)
                .build();

        List<CategoryEntity> expectedEntities = Arrays.asList(
                new CategoryEntity(categoryMap.get("Humor"), "Humor"),
                new CategoryEntity(categoryMap.get("Sports"), "Sports")
        );
        List<CategoryEntity> dbLoadedEntities = manager.search(searchParams);
        clearIds(dbLoadedEntities);

        assertEquals(expectedEntities, dbLoadedEntities);
    }

    @Test
    void Should_LoadCorrectEntities_When_SearchingWithImplicitCriteria() {
        CategorySearchParamsDto searchParams = CategorySearchParamsDto.builder()
                .subject("tion")
                .orderBy("subject")
                .build();

        List<CategoryEntity> expectedEntities = Arrays.asList(
                new CategoryEntity(categoryMap.get("Fiction"), "Fiction"),
                new CategoryEntity(categoryMap.get("Historical Fiction"), "Historical Fiction")
        );
        List<CategoryEntity> dbLoadedEntities = manager.search(searchParams);
        clearIds(dbLoadedEntities);

        assertEquals(expectedEntities, dbLoadedEntities);
    }

    @Test
    void ShouldNot_Load_When_SearchingWithInvalidCriteria() {
        CategorySearchParamsDto searchParams = CategorySearchParamsDto.builder()
                .orderBy("NonExistentField")
                .build();

        assertThrows(CategoryIllegalSearchException.class, () -> manager.search(searchParams));
    }

    @Test
    void Should_CountCorrectly() {
        assertEquals(0, manager.count("NonExistent Code", null));
        assertEquals(0, manager.count(categoryMap.get("Fiction"), "or"));
        assertEquals(0, manager.count(null, "NonExistent Pattern"));
        assertEquals(1, manager.count(categoryMap.get("Humor"), "or"));
        assertEquals(5, manager.count(null, "or"));
        assertEquals(5, manager.count(null, "OR"));
    }

    private void clearIds(List<CategoryEntity> dbLoadedEntities) {
        for (CategoryEntity entity : dbLoadedEntities) {
            entity.setId(0);
        }
    }
}
