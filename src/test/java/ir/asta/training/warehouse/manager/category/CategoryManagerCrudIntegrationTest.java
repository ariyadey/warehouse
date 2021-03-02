package ir.asta.training.warehouse.manager.category;

import ir.asta.training.warehouse.dto.CategorySaveRequestDto;
import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class CategoryManagerCrudIntegrationTest {

    @Autowired
    CategoryManager manager;

    @Test
    void Should_CRUD_When_SubjectHasText() {
        CategorySaveRequestDto givenDto = new CategorySaveRequestDto("A test subject to save");

        final CategoryEntity dbSavedEntity = manager.save(givenDto);
        final String generatedCode = dbSavedEntity.getCode();
        CategoryEntity dbLoadedEntity = manager.loadByCode(generatedCode);

        assertEquals(dbSavedEntity, dbLoadedEntity);


        givenDto = new CategorySaveRequestDto("A test subject to update");

        final CategoryEntity dbUpdatedEntity = manager.update(generatedCode, givenDto);
        dbLoadedEntity = manager.loadByCode(generatedCode);

        assertEquals(dbUpdatedEntity, dbLoadedEntity);


        assertDoesNotThrow(() -> manager.deleteByCode(generatedCode));
        assertThrows(CategoryNotFoundException.class, () -> manager.loadByCode(generatedCode));
    }

    @Test
    void ShouldNot_Save_When_SubjectDoesntHaveText() {
        CategorySaveRequestDto givenDto = new CategorySaveRequestDto("    ");

        Executable saveAndLoad = () -> {
            CategoryEntity dbSavedEntity = manager.save(givenDto);
            manager.loadByCode(dbSavedEntity.getCode());
        };

        assertThrows(ConstraintViolationException.class, saveAndLoad);
    }

    @Test
    void ShouldNot_Load_When_EntityWithSpecifiedCodeDoesNotExist() {
        assertThrows(CategoryNotFoundException.class, () -> manager.loadByCode("Entity with this code does not exist"));
    }

    @Test
    void ShouldNot_Update_When_EntityWithSpecifiedCodeDoesNotExist() {
        CategorySaveRequestDto givenDto = new CategorySaveRequestDto("A test subject to update");

        assertThrows(CategoryNotFoundException.class,
                () -> manager.update("Entity with this code does not exist", givenDto));
    }

    @Test
    void ShouldNot_Delete_When_EntityWithSpecifiedCodeDoesNotExist() {
        assertThrows(CategoryNotFoundException.class,
                () -> manager.deleteByCode("Entity with this code does not exist"));
    }
}
