package ir.asta.training.warehouse.manager.category;

import ir.asta.training.warehouse.dao.CategoryDao;
import ir.asta.training.warehouse.dto.CategorySaveRequestDto;
import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.category.exception.CategoryNotProcessableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        validate(dto);
        final CategoryEntity entity = new CategoryEntity(UUID.randomUUID().toString(), dto.getSubject());
        return dao.save(entity);
    }

    @Transactional
    public CategoryEntity loadByCode(String code) {
        return dao.load(code);
    }

    private void validate(CategorySaveRequestDto dto) {
        if (dto == null || !StringUtils.hasText(dto.getSubject())) {
            log.debug("The category dto is invalid and can't be saved into DB: {}", dto);
            throw new CategoryNotProcessableException();
        }
    }
}
