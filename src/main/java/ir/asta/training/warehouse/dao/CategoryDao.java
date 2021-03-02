package ir.asta.training.warehouse.dao;

import ir.asta.training.warehouse.dto.CategorySearchParamsDto;
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
        log.info("The category was removed from DB: {}", entity);
    }

    public List<CategoryEntity> search(CategorySearchParamsDto dto) {
        log.debug("Searching in DB according to parameters: {}", dto);
        String queryString = String.format("select c from CategoryEntity c\n" +
                                           "%s" +
                                           " %s %s %s\n" +
                                           "order by %s c.id %s",
                dto.getCode() == null && dto.getSubject() == null ? "" : "where",
                dto.getCode() == null ? "" : "c.code = :code",
                dto.getCode() == null || dto.getSubject() == null ? "" : "and",
                dto.getSubject() == null ? "" : "lower(c.subject) like concat('%', lower(:subject), '%')",
                dto.getOrderBy() == null ? "" : dto.getOrderBy() + ", ",
                dto.getSortDirection());

        TypedQuery<CategoryEntity> typedQuery = entityManager.createQuery(queryString, CategoryEntity.class);
        if (dto.getCode() != null) {
            typedQuery.setParameter("code", dto.getCode());
        }
        if (dto.getSubject() != null) {
            typedQuery.setParameter("subject", dto.getSubject());
        }
        typedQuery.setMaxResults(dto.getPageSize());
        typedQuery.setFirstResult(((dto.getPageNumber() - 1) * typedQuery.getMaxResults()));

        List<CategoryEntity> searchResult = typedQuery.getResultList();
        log.debug("Results are fetched according to search criteria: \n{}", searchResult);
        return searchResult;
    }


    public long count(String code, String subject) {
        log.debug("Counting categories with code: {} and subject: {}", code, subject);
        String queryString = String.format("select count(c) from CategoryEntity c\n" +
                                           "%s %s %s %s",
                code == null && subject == null ? "" : "where",
                code == null ? "" : "c.code = :code",
                code == null || subject == null ? "" : "and",
                subject == null ? "" : "lower(c.subject) like concat('%', lower(:subject), '%')");
        Query query = entityManager.createQuery(queryString);

        if (code != null) {
            query.setParameter("code", code);
        }
        if (subject != null) {
            query.setParameter("subject", subject);
        }

        final long result = (long) query.getSingleResult();
        log.trace("The result of counting categories in DB is: {}", result);
        return result;
    }
}
