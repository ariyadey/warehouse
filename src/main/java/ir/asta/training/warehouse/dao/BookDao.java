package ir.asta.training.warehouse.dao;

import ir.asta.training.warehouse.entity.BookEntity;
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
    public void save(BookEntity bookEntity) {
        log.info("going to save BookEntity to db :{}", bookEntity);
        entityManager.persist(bookEntity);
        log.info("book entity is saved to database. Id is : {}", bookEntity.getId());
    }
}
