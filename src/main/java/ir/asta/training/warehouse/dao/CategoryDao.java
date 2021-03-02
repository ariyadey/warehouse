package ir.asta.training.warehouse.dao;

import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.category.exception.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Component
@Slf4j
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CategoryEntity save(CategoryEntity entity) {
        log.debug("Saving the category to DB... {}", entity);
        entityManager.persist(entity);
        log.info("The category saved to DB: {}", entity);
        return entity;
    }

    public CategoryEntity load(String code) {
        try {
            return doLoad(code);
        } catch (NoResultException ex) {
            throw new CategoryNotFoundException(code);
        }
    }

    private CategoryEntity doLoad(String code) {
        log.debug("Loading the category from DB with code: {}", code);
        String queryString = "select c from CategoryEntity c where c.code = :code";
        TypedQuery<CategoryEntity> typedQuery = entityManager.createQuery(queryString, CategoryEntity.class);
        typedQuery.setParameter("code", code);
        CategoryEntity entity = typedQuery.getSingleResult();
        log.debug("The category is loaded from DB: {}", entity);
        return entity;
    }

    public CategoryEntity update(CategoryEntity entity) {
        log.debug("Updating the category in DB with code: {} and old subject: {}", entity.getCode(),
                entity.getSubject());
        final CategoryEntity dbLoadedEntity = load(entity.getCode());
        dbLoadedEntity.setSubject(entity.getSubject());
        log.info("The category updated in DB: {}", dbLoadedEntity);
        return dbLoadedEntity;
    }

    public void remove(String code) {
        log.debug("Removing the category from DB with code: {}", code);
        CategoryEntity entity = load(code);
        entityManager.remove(entity);
        log.info("Category was removed from DB: {}", entity);
    }
}
