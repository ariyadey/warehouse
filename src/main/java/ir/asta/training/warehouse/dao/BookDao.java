package ir.asta.training.warehouse.dao;

import ir.asta.training.warehouse.entity.BookEntity;
import ir.asta.training.warehouse.manager.book.exception.BookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Slf4j
public class BookDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public long save(BookEntity entity) {
        entityManager.persist(entity);
        return entity.getId();
    }

    public BookEntity load(long id) {
        final BookEntity entity = entityManager.find(BookEntity.class, id);
        if (entity == null) {
            throw new BookNotFoundException();
        }
        return entity;
    }
}
