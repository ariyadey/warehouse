package ir.asta.training.warehouse.manager.book;

import ir.asta.training.warehouse.dao.BookDao;
import ir.asta.training.warehouse.dto.BookDto;
import ir.asta.training.warehouse.manager.book.exception.BookNotProcessableException;
import ir.asta.training.warehouse.mapper.BookMapper;
import ir.asta.training.warehouse.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public BookDto load(String id) {
        return mapper.toDto(dao.load(Long.parseLong(id)));
    }

    public long save(BookDto dto) {
        long entityId;
        if (isDtoValid(dto)) {
            if (!ReflectionUtil.hasNullField(dto)) {
                entityId = dao.save(mapper.toEntity(dto));
            } else {
                entityId = dao.save(mapper.toEntity(itBookApiProxy.load(dto.getIsbn13())));
            }
        } else {
            throw new BookNotProcessableException();
        }
        return entityId;
    }

    private boolean isDtoValid(BookDto dto) {
        return isbnValidator.isIsbn13Valid(dto.getIsbn13()) &&
               ((dto.getIsbn10() == null) || isbnValidator.isIsbn10Valid(dto.getIsbn10()));
    }
}
