package ir.asta.training.warehouse.manager.category;

import ir.asta.training.warehouse.dto.CategorySaveRequestDto;
import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.category.exception.CategoryNotFoundException;
import ir.asta.training.warehouse.manager.category.exception.CategoryNotProcessableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class CategoryIntegrationTest {

    @Autowired
    CategoryManager manager;

    @Test
    void Should_SaveLoadDelete_When_SubjectHasText() {
        CategorySaveRequestDto givenDto = new CategorySaveRequestDto("A test subject");

        CategoryEntity dbSavedEntity = manager.save(givenDto);
        CategoryEntity dbLoadedEntity = manager.loadByCode(dbSavedEntity.getCode());

        assertEquals(dbSavedEntity, dbLoadedEntity);

        assertDoesNotThrow(() -> manager.deleteByCode(dbSavedEntity.getCode()));
        assertThrows(CategoryNotFoundException.class, () -> manager.loadByCode(dbSavedEntity.getCode()));
    }

    @Test
    void ShouldNot_Save_When_SubjectDoesntHaveText() {
        CategorySaveRequestDto givenDto = new CategorySaveRequestDto("    ");

        assertThrows(CategoryNotProcessableException.class, () -> manager.save(givenDto));
    }

    @Test
    void ShouldNot_Load_When_EntityWithSpecifiedCodeDoesNotExist() {
        assertThrows(CategoryNotFoundException.class, () -> manager.loadByCode("Entity with this code does not exist"));
    }

    @Test
    void ShouldNot_Delete_When_EntityWithSpecifiedCodeDoesNotExist() {
        assertThrows(CategoryNotFoundException.class,
                () -> manager.deleteByCode("Entity with this code does not exist"));
    }
}
