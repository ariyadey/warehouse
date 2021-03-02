package ir.asta.training.warehouse.manager.category;

import ir.asta.training.warehouse.dao.CategoryDao;
import ir.asta.training.warehouse.dto.CategorySaveRequestDto;
import ir.asta.training.warehouse.entity.CategoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class CategoryManager {

    private final CategoryDao dao;

    @Autowired
    public CategoryManager(CategoryDao dao) {
        this.dao = dao;
    }

    @Transactional
    public CategoryEntity save(CategorySaveRequestDto dto) {
        final CategoryEntity entity = new CategoryEntity(UUID.randomUUID().toString(), dto.getSubject());
        return dao.save(entity);
    }

    @Transactional(readOnly = true)
    public CategoryEntity loadByCode(String code) {
        return dao.load(code);
    }

    @Transactional
    public CategoryEntity update(String code, CategorySaveRequestDto dto) {
        final CategoryEntity entity = new CategoryEntity(code, dto.getSubject());
        return dao.update(entity);
    }

    @Transactional
    public void deleteByCode(String code) {
        dao.remove(code);
    }
}
