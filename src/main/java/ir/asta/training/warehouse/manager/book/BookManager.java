package ir.asta.training.warehouse.manager.book;

import ir.asta.training.warehouse.dao.BookDao;
import ir.asta.training.warehouse.dto.BookDto;
import ir.asta.training.warehouse.manager.book.exception.BookNotProcessableException;
import ir.asta.training.warehouse.mapper.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static ir.asta.training.warehouse.util.ReflectionUtil.*;

@Slf4j
@Component
public class BookManager {
    private final BookDao dao;
    private final BookMapper mapper;
    private final ItBookApiProxy itBookApiProxy;
    private final IsbnValidator isbnValidator;

    @Autowired
    public BookManager(BookDao dao,
                       BookMapper mapper,
                       ItBookApiProxy itBookApiProxy,
                       IsbnValidator isbnValidator) {
        this.dao = dao;
        this.mapper = mapper;
        this.itBookApiProxy = itBookApiProxy;
        this.isbnValidator = isbnValidator;
    }

    @Transactional
    public BookDto load(long id) {
        return mapper.toDto(dao.load(id));
    }

    public long save(BookDto dto) {
        validateDto(dto);
        long entityId;
        // TODO: 20/02/2021 Don't overrdie given data
        if (hasNullField(dto)) {
            entityId = dao.save(mapper.toEntity(itBookApiProxy.load(dto.getIsbn13())));
        } else {
            entityId = dao.save(mapper.toEntity(dto));
        }
        return entityId;
    }

    private void validateDto(BookDto dto) {
        if (!(isbnValidator.isIsbn13Valid(dto.getIsbn13()) &&
              ((dto.getIsbn10() == null) || isbnValidator.isIsbn10Valid(dto.getIsbn10())))) {
            throw new BookNotProcessableException();
        }
    }
}
