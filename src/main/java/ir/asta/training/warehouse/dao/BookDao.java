package ir.asta.training.warehouse.dao;

import ir.asta.training.warehouse.entity.BookEntity;
import ir.asta.training.warehouse.manager.book.exception.BookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Slf4j
public class BookDao {

    @PersistenceContext
    private EntityManager entityManager;

    public long save(BookEntity entity) {
        log.debug("Book before saving to DB: {}", entity);
        entityManager.persist(entity);
        log.debug("Book before saving to DB: {}", entity);
        return entity.getId();
    }

    public BookEntity load(long id) {
        final BookEntity entity = entityManager.find(BookEntity.class, id);
        if (entity == null) {
            log.debug("Book with id: {} not found in DB", id);
            throw new BookNotFoundException();
        }
        log.debug("A book is loaded from DB: {}", entity);
        return entity;
    }
}
