package ir.asta.training.warehouse.service;

import ir.asta.training.warehouse.dto.BookDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static javax.ws.rs.core.Response.Status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceIntegrationTest {

    @LocalServerPort
    int port;
    Client client;

    @BeforeAll
    void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    void terminate() {
        client.close();
    }

    @Test
    void testPostBook() {
        assertEquals(Status.NO_CONTENT, getStatus(BookDto
                .builder()
                .title("Animal Farm")
                .isbn10("7594651230")
                .isbn13("7954613054894")
                .price(BigDecimal.valueOf(30))
                .build()));
        assertEquals(Status.NO_CONTENT, getStatus(BookDto
                .builder()
                .title("The Subtle Art of Not Giving a Fuck")
                .isbn10("7532651230")
                .isbn13("7310579103731")
                .price(BigDecimal.valueOf(25.5))
                .build()));
        assertEquals(Status.NO_CONTENT, getStatus(BookDto
                .builder()
                .title("Shahnameh")
                .isbn10("0315651230")
                .isbn13("7912389435659")
                .price(BigDecimal.valueOf(55.55))
                .build()));
        assertEquals(Status.NO_CONTENT, getStatus(BookDto
                .builder()
                .title(null)
                .isbn10("1979100316")
                .price(BigDecimal.valueOf(99.99))
                .build()));
    }

    private Response.StatusType getStatus(BookDto bookDto) {
        return client
                .target(String.format("http://localhost:%d/warehouse/api/book", port))
                .request(MediaType.TEXT_PLAIN)
                .post(Entity.json(bookDto))
                .getStatusInfo();
    }
}