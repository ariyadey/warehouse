package ir.asta.training.warehouse.manager.category;

import ir.asta.training.warehouse.dao.CategoryDao;
import ir.asta.training.warehouse.dto.CategorySaveRequestDto;
import ir.asta.training.warehouse.dto.CategorySearchParamsDto;
import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.SortDirection;
import ir.asta.training.warehouse.manager.category.exception.CategoryIllegalSearchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static ir.asta.training.warehouse.util.ReflectionUtil.containsField;

@Component
public class CategoryManager {

    private final CategoryDao dao;
    private final Environment env;

    @Autowired
    public CategoryManager(CategoryDao dao, Environment env) {
        this.dao = dao;
        this.env = env;
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

    @Transactional(readOnly = true)
    public List<CategoryEntity> search(CategorySearchParamsDto dto) {
        validate(dto);
        return dao.search(format(dto));
    }

    @Transactional(readOnly = true)
    public long count(String code, String subject) {
        return dao.count(code, subject);
    }

    private CategorySearchParamsDto format(CategorySearchParamsDto dto) {
        return CategorySearchParamsDto.builder()
                .subject(dto.getSubject())
                .code(dto.getCode())
                .pageSize(dto.getPageSize() == null || dto.getPageSize() < 0
                        ? env.getRequiredProperty("warehouse.search.default-page-size", Integer.class)
                        : dto.getPageSize())
                .pageNumber(dto.getPageNumber() == null || dto.getPageNumber() < 0
                        ? env.getRequiredProperty("warehouse.search.default-page-number", Integer.class)
                        : dto.getPageNumber())
                .orderBy(dto.getOrderBy())
                .sortDirection(dto.getSortDirection() == null ? SortDirection.ASC : dto.getSortDirection())
                .build();
    }

    private void validate(CategorySearchParamsDto dto) {
        if (!containsField(CategoryEntity.class, dto.getOrderBy())) {
            throw new CategoryIllegalSearchException(dto);
        }
    }
}

