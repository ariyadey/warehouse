package ir.asta.training.warehouse.manager.category;

import ir.asta.training.warehouse.dto.CategorySaveRequestDto;
import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.category.exception.CategoryNotProcessableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class CategoryIntegrationTest {

    @Autowired
    CategoryManager manager;

    @Test
    void Should_Save_When_SubjectHasText() {
        CategorySaveRequestDto givenDto = new CategorySaveRequestDto("A test subject");

        CategoryEntity dbSavedEntity = manager.save(givenDto);
        CategoryEntity dbLoadedEntity = manager.loadByCode(dbSavedEntity.getCode());

        assertEquals(dbSavedEntity, dbLoadedEntity);
    }

    @Test
    void ShouldNot_Save_When_SubjectDoesntHaveText() {
        CategorySaveRequestDto givenDto = new CategorySaveRequestDto("    ");

        assertThrows(CategoryNotProcessableException.class, () -> manager.save(givenDto));
    }


}
