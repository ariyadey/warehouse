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

import static ir.asta.training.warehouse.service.ExtendedStatus.*;
import static javax.ws.rs.core.Response.Status.*;

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
    public Response load(@PathParam("id") String id) {
        Response response;
        try {
            final BookDto dto = manager.load(id);
            response = Response.ok(dto).build();
        } catch (BookNotFoundException exception) {
            response = Response.status(NOT_FOUND).build();
        }
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response save(BookDto bookDto) {
        Response response;
        try {
            final long id = manager.save(bookDto);
            response = Response.status(CREATED).entity(id).build();
        } catch (BookNotProcessableException exception) {
            response = Response.status(UNPROCESSABLE_ENTITY).build();
        } catch (BookNotFoundException exception) {
            response = Response.status(NOT_FOUND).build();
        } catch (ItBookServiceUnavailableException exception) {
            response = Response.status(SERVICE_UNAVAILABLE).build();
        }
        return response;
    }
}
