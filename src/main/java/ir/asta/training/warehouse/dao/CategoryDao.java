package ir.asta.training.warehouse.dao;

import ir.asta.training.warehouse.entity.CategoryEntity;
import ir.asta.training.warehouse.manager.category.exception.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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

    public void remove(String code) {
        log.debug("Removing the category from DB with code: {}", code);
        CategoryEntity entity = load(code);
        entityManager.remove(entity);
        log.info("Category was removed from DB: {}", entity);
    }
}
