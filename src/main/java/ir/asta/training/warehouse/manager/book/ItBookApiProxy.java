package ir.asta.training.warehouse.manager.book;

import ir.asta.training.warehouse.dto.ItBookApiDto;
import ir.asta.training.warehouse.manager.book.exception.BookNotFoundException;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import static java.lang.String.format;

// TODO: 19/02/2021 What about external problems such as network timeouts?
@Component
public class ItBookApiProxy {
    private Client client;

    public ItBookApiDto load(String isbn13) {
        if (client == null) {
            client = ClientBuilder.newClient();
        }

        final ItBookApiDto dto = client
                .target(format("https://api.itbook.store/1.0/books/%s", isbn13))
                .request(MediaType.APPLICATION_JSON)
                .get(ItBookApiDto.class);

        if (dto.getError().equals("0")) {
            return dto;
        } else {
            throw new BookNotFoundException();
        }
    }
}
