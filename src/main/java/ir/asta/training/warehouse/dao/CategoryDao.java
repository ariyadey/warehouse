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
        log.debug("Category before saving to DB: {}", entity);
        entityManager.persist(entity);
        log.debug("Category after saving to DB: {}", entity);
        return entity;
    }

    public CategoryEntity load(String code) {
        String queryString = "select c from CategoryEntity c where c.code = :code";
        TypedQuery<CategoryEntity> typedQuery = entityManager.createQuery(queryString, CategoryEntity.class);
        typedQuery.setParameter("code", code);
        try {
            CategoryEntity entity = typedQuery.getSingleResult();
            log.debug("A category is loaded from DB: {}", entity);
            return entity;
            // TODO: 26/02/2021 Shouldn't I detach it?
        } catch (NoResultException ex) {
            log.debug("Category with code: {} not found in DB", code);
            throw new CategoryNotFoundException();
        }
    }

    public void remove(String code) {
        entityManager.remove(load(code));
    }
}
