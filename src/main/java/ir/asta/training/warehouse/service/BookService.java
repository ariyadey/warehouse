package ir.asta.training.warehouse.service;

import ir.asta.training.warehouse.dto.BookDto;
import ir.asta.training.warehouse.manager.book.BookManager;
import ir.asta.training.warehouse.manager.book.exception.BookNotFoundException;
import ir.asta.training.warehouse.manager.book.exception.BookNotProcessableException;
import ir.asta.training.warehouse.manager.book.exception.ItBookServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;

import static ir.asta.training.warehouse.service.ExtendedStatus.UNPROCESSABLE_ENTITY;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Slf4j
@Component
@Path("book")
public class BookService {

    private final BookManager manager;

    @Autowired
    public BookService(BookManager manager) {
        this.manager = manager;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response load(@PathParam("id") long id) {
        ResponseBuilder response;
        try {
            final BookDto dto = manager.load(id);
            response = Response.ok(dto);
        } catch (BookNotFoundException exception) {
            response = Response.status(NOT_FOUND);
        }
        return response.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(BookDto bookDto) {
        ResponseBuilder response;
        try {
            final long id = manager.save(bookDto);
            response = Response.created(URI.create(String.valueOf(id)));
        } catch (BookNotProcessableException exception) {
            response = Response.status(UNPROCESSABLE_ENTITY);
        } catch (BookNotFoundException exception) {
            response = Response.status(NOT_FOUND);
        } catch (ItBookServiceUnavailableException exception) {
            response = Response.serverError();
        }
        return response.build();
    }
}
