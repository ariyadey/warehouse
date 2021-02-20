package ir.asta.training.warehouse.service;

import ir.asta.training.warehouse.dto.BookDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;

import static javax.ws.rs.core.Response.Status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceIntegrationTest {

    private final String originalTitle = "RESTful Java with JAX-RS 2.0, 2nd Edition";
    private final String mockTitle = "This is a fake title for testing purposes";
    private final String itBookExistingIsbn13 = "9781449361341";
    private final String itBookNonExistingIsbn13 = "9786227233797";
    private final String validIsbn10 = "144936134X";
    private final String invalidIsbn10 = "1449361340";
    private final String validIsbn13 = itBookNonExistingIsbn13;
    private final String invalidIsbn13 = "9781449361340";
    private final BigDecimal originalPrice = BigDecimal.valueOf(22.00);
    private final BigDecimal mockPrice = BigDecimal.valueOf(1.000);

    @LocalServerPort
    private int port;

    private Client client;

    @BeforeAll
    void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    void terminate() {
        client.close();
    }

    @Test
    void Should_SaveBook_When_BothIsbnsAreValid_And_OtherFieldsCompleted() {
        final BookDto dto = BookDto
                .builder()
                .title(mockTitle)
                .isbn10(validIsbn10)
                .isbn13(validIsbn13)
                .price(mockPrice)
                .build();

        assertSaves(dto);
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsValid_And_OtherFieldsNotCompleted() {
        final BookDto dto = BookDto
                .builder()
                .isbn10(validIsbn10)
                .isbn13(itBookExistingIsbn13)
                .build();

        assertSaves(dto);
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsNotGiven() {
        final BookDto dto = BookDto
                .builder()
                .isbn13(itBookExistingIsbn13)
                .price(mockPrice)
                .build();

        assertSaves(dto);
    }

    @Test
    void ShouldNot_SaveBook_When_OneOfIsbnsAreInvalid() {
        final BookDto dto = BookDto
                .builder()
                .title(mockTitle)
                .isbn10(invalidIsbn10)
                .isbn13(invalidIsbn13)
                .price(mockPrice)
                .build();

        assertFailsSaving(ExtendedStatus.UNPROCESSABLE_ENTITY, dto);
    }

    @Test
    void ShouldNot_SaveBook_When_Isbn13DoesNotExist_And_Isbn10IsNotInvalid_And_OtherFieldsNotCompleted() {
        final BookDto dto = BookDto
                .builder()
                .isbn10(validIsbn10)
                .isbn13(itBookNonExistingIsbn13)
                .build();

        assertFailsSaving(Status.NOT_FOUND, dto);
    }

    private void assertSaves(BookDto dto) {
        final Response postResponse = client
                .target(String.format("http://localhost:%d/warehouse/api/book", port))
                .request()
                .post(Entity.json(dto));

        assertEquals(Status.CREATED, postResponse.getStatusInfo());


//        assertEquals(URI.create(String.format("http://localhost:%s/warehouse/api/1", port)), postResponse.getLocation());
        final Response getResponse = client
                .target(String.format("http://localhost:%s/warehouse/api/1", port))
                .path("1")
                .request(MediaType.APPLICATION_JSON)
                .get();
        final BookDto expectedDto = BookDto.builder()
                .title(dto.getTitle() == null ? originalTitle : dto.getTitle())
                .isbn10(dto.getIsbn10() == null ? validIsbn10 : dto.getIsbn10())
                .isbn13(dto.getIsbn13() == null ? itBookExistingIsbn13 : dto.getIsbn13())
                .price(dto.getPrice() == null ? originalPrice : dto.getPrice())
                .build();

        assertEquals(Status.OK, getResponse.getStatusInfo());
        assertEquals(expectedDto, getResponse.readEntity(BookDto.class));
    }

    private void assertFailsSaving(Response.StatusType expectedStatus, BookDto dto) {
        final Response postResponse = client
                .target(String.format("http://localhost:%d/warehouse/api/book", port))
                .request(MediaType.TEXT_PLAIN)
                .post(Entity.json(dto));

        assertEquals(expectedStatus.getStatusCode(), postResponse.getStatus());
    }
}