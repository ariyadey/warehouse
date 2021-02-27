package ir.asta.training.warehouse.manager.book;

import ir.asta.training.warehouse.dto.ItBookApiDto;
import ir.asta.training.warehouse.manager.book.exception.BookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import static java.lang.String.format;

@Component
@Slf4j
public class ItBookApiProxy {

    Client client;

    @Autowired
    public ItBookApiProxy(Client client) {
        this.client = client;
    }

    public ItBookApiDto load(String isbn13) {
        log.debug("Retrieving data from api.itbook.store...");
        final ItBookApiDto dto = client
                .target(format("https://api.itbook.store/1.0/books/%s", isbn13))
                .request(MediaType.APPLICATION_JSON)
                .get(ItBookApiDto.class);
        validateDtoHasNoErrors(dto);
        return dto;
    }

    private void validateDtoHasNoErrors(ItBookApiDto dto) {
        if (!dto.getError().equals("0")) {
            throw new BookNotFoundException();
        }
    }
}
