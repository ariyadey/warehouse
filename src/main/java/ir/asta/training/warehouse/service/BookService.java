package ir.asta.training.warehouse.service;

import ir.asta.training.warehouse.dao.BookDao;
import ir.asta.training.warehouse.dto.BookDto;
import ir.asta.training.warehouse.entity.BookEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Slf4j
@Component
@Path("book")
public class BookService {

    private final BookDao bookDao;

    @Autowired
    public BookService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @POST
    public Response save(BookDto bookDto) {
        log.info(String.format("Request received to %s for %s = %s",
                BookService.class.getSimpleName(),
                BookDto.class.getSimpleName(),
                bookDto));
        bookDao.save(BookEntity.builder()
                             .title(bookDto.getTitle())
                             .isbn10(bookDto.getIsbn10())
                             .isbn13(bookDto.getIsbn13())
                             .build());
        return Response.noContent().build();
    }
}
