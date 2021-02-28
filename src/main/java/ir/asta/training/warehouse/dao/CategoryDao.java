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
        log.debug("Category is going to save to DB: {}", entity);
        entityManager.persist(entity);
        log.info("Category saved to DB: {}", entity);
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
        String queryString = "select c from CategoryEntity c where c.code = :code";
        TypedQuery<CategoryEntity> typedQuery = entityManager.createQuery(queryString, CategoryEntity.class);
        typedQuery.setParameter("code", code);
        CategoryEntity entity = typedQuery.getSingleResult();
        log.debug("A category is loaded from DB: {}", entity);
        return entity;
    }

    public void remove(String code) {
        CategoryEntity entity = load(code);
        entityManager.remove(entity);
        log.info("Category was removed from DB: {}", entity);
    }
}
