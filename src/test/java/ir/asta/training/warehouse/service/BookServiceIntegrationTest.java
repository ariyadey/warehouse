package ir.asta.training.warehouse.service;

import ir.asta.training.warehouse.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;

import static javax.ws.rs.core.Response.Status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceIntegrationTest {

    private final String targetBookTitle = "RESTful Java with JAX-RS 2.0, 2nd Edition";
    private final String targetBookIsbn13 = "9781449361341";
    private final String targetBookIsbn10 = "144936134X";
    private final BigDecimal targetBookPrice = BigDecimal.valueOf(22.00);
    private final String unrelatedTitle = "This is another title unrelated to the ISBN13 of the target book";
    private final String validButNotInItBookIsbn13 = "9786227233797";
    private final String invalidIsbn10 = "1449361340";
    private final BigDecimal unrelatedPrice = BigDecimal.valueOf(1.000);

    @LocalServerPort
    private int port;

    private final URI bookServiceUri = URI.create(String.format("http://localhost:%d/warehouse/api/book", port));

    @Autowired
    private Client client;

    private Response requestToLoadDto(URI location) {
        return client
                .target(location)
                .request(MediaType.APPLICATION_JSON)
                .get();
    }

    private Response requestToSaveDto(BookDto givenDto) {
        return client
                .target(bookServiceUri)
                .request()
                .post(Entity.json(givenDto));
    }

    @Test
    void Should_SaveBook_When_BothIsbnsAreValid_And_OtherFieldsCompleted() {
        final BookDto givenDto = BookDto
                .builder()
                .title(unrelatedTitle)
                .isbn10(targetBookIsbn10)
                .isbn13(targetBookIsbn13)
                .price(unrelatedPrice)
                .build();

        final Response postResponse = requestToSaveDto(givenDto);

        assertEquals(Status.CREATED, postResponse.getStatusInfo());


        final Response getResponse = requestToLoadDto(postResponse.getLocation());
        final BookDto dtoFromServer = getResponse.readEntity(BookDto.class);

        assertEquals(Status.OK, getResponse.getStatusInfo());
        assertEquals(unrelatedTitle, dtoFromServer.getTitle());
        assertEquals(targetBookIsbn10, dtoFromServer.getIsbn10());
        assertEquals(targetBookIsbn13, dtoFromServer.getIsbn13());
        assertEquals(unrelatedPrice, dtoFromServer.getPrice());
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsValid_And_OtherFieldsNotCompleted() {
        final BookDto givenDto = BookDto
                .builder()
                .isbn10(targetBookIsbn10)
                .isbn13(targetBookIsbn13)
                .build();

        final Response postResponse = requestToSaveDto(givenDto);

        assertEquals(Status.CREATED, postResponse.getStatusInfo());


        final Response getResponse = requestToLoadDto(postResponse.getLocation());
        final BookDto dtoFromServer = getResponse.readEntity(BookDto.class);

        assertEquals(Status.OK, getResponse.getStatusInfo());
        assertEquals(targetBookTitle, dtoFromServer.getTitle());
        assertEquals(targetBookIsbn10, dtoFromServer.getIsbn10());
        assertEquals(targetBookIsbn13, dtoFromServer.getIsbn13());
        assertEquals(targetBookPrice, dtoFromServer.getPrice());
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsNotGiven() {
        final BookDto givenDto = BookDto
                .builder()
                .isbn13(targetBookIsbn13)
                .price(unrelatedPrice)
                .build();

        final Response postResponse = requestToSaveDto(givenDto);

        assertEquals(Status.CREATED, postResponse.getStatusInfo());


        final Response getResponse = requestToLoadDto(postResponse.getLocation());
        final BookDto dtoFromServer = getResponse.readEntity(BookDto.class);

        assertEquals(Status.OK, getResponse.getStatusInfo());
        assertEquals(targetBookTitle, dtoFromServer.getTitle());
        assertEquals(targetBookIsbn10, dtoFromServer.getIsbn10());
        assertEquals(targetBookIsbn13, dtoFromServer.getIsbn13());
        assertEquals(unrelatedPrice, dtoFromServer.getPrice());
    }

    @Test
    void ShouldNot_SaveBook_When_OneOfIsbnsAreInvalid() {
        final BookDto givenDto = BookDto
                .builder()
                .title(unrelatedTitle)
                .isbn10(invalidIsbn10)
                .isbn13(targetBookIsbn13)
                .price(unrelatedPrice)
                .build();

        final Response postResponse = requestToSaveDto(givenDto);

        assertEquals(ExtendedStatus.UNPROCESSABLE_ENTITY.getStatusCode(), postResponse.getStatus());
    }

    @Test
    void ShouldNot_SaveBook_When_Isbn13DoesNotExist_And_Isbn10IsNotInvalid_And_OtherFieldsNotCompleted() {
        final BookDto givenDto = BookDto
                .builder()
                .isbn10(targetBookIsbn10)
                .isbn13(validButNotInItBookIsbn13)
                .build();

        final Response postResponse = requestToSaveDto(givenDto);

        assertEquals(Status.NOT_FOUND, postResponse.getStatusInfo());
    }

    @Test
    void ShouldNot_LoadBook_When_BookWithSpecifiedLocationDoesntExist() {
        final URI uri = URI.create(bookServiceUri + "/" + Long.MAX_VALUE);

        final Response getResponse = requestToLoadDto(uri);

        assertEquals(Status.NOT_FOUND, getResponse.getStatusInfo());
    }
}