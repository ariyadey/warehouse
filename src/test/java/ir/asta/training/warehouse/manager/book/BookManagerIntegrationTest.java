package ir.asta.training.warehouse.manager.book;

import ir.asta.training.warehouse.dto.BookDto;
import ir.asta.training.warehouse.dto.ItBookApiDto;
import ir.asta.training.warehouse.manager.book.exception.BookNotFoundException;
import ir.asta.training.warehouse.manager.book.exception.BookNotProcessableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
class BookManagerIntegrationTest {

    private final String targetBookTitle = "RESTful Java with JAX-RS 2.0, 2nd Edition";
    private final String targetBookIsbn13 = "9781449361341";
    private final String targetBookIsbn10 = "144936134X";
    private final BigDecimal targetBookPrice = BigDecimal.valueOf(22.00);
    private final String unrelatedTitle = "This is another title unrelated to the ISBN13 of the target book";
    private final String validButNotInItBookIsbn13 = "9786227233797";
    private final String invalidIsbn10 = "1449361340";
    private final BigDecimal unrelatedPrice = BigDecimal.valueOf(1.000);

    @Autowired
    private BookManager manager;

    @MockBean
    private ItBookApiProxy itBookApi;

    private ItBookApiDto getTargetDto() {
        return ItBookApiDto.builder()
                .title(targetBookTitle)
                .isbn10(targetBookIsbn10)
                .isbn13(targetBookIsbn13)
                .price(String.valueOf(targetBookPrice))
                .build();
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

        final long entityId = manager.save(givenDto);
        final BookDto dtoFromDb = manager.load(entityId);

        assertEquals(unrelatedTitle, dtoFromDb.getTitle());
        assertEquals(targetBookIsbn10, dtoFromDb.getIsbn10());
        assertEquals(targetBookIsbn13, dtoFromDb.getIsbn13());
        assertEquals(unrelatedPrice, dtoFromDb.getPrice());
        verify(itBookApi, never()).load(anyString());
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsValid_And_OtherFieldsNotCompleted() {
        when(itBookApi.load(targetBookIsbn13)).thenReturn(getTargetDto());
        final BookDto givenDto = BookDto
                .builder()
                .isbn10(targetBookIsbn10)
                .isbn13(targetBookIsbn13)
                .build();

        final long entityId = manager.save(givenDto);
        final BookDto dtoFromDb = manager.load(entityId);

        assertEquals(targetBookTitle, dtoFromDb.getTitle());
        assertEquals(targetBookIsbn10, dtoFromDb.getIsbn10());
        assertEquals(targetBookIsbn13, dtoFromDb.getIsbn13());
        assertEquals(targetBookPrice, dtoFromDb.getPrice());
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsNotGiven() {
        when(itBookApi.load(targetBookIsbn13)).thenReturn(getTargetDto());
        final BookDto givenDto = BookDto
                .builder()
                .isbn13(targetBookIsbn13)
                .price(unrelatedPrice)
                .build();

        final long entityId = manager.save(givenDto);
        final BookDto dtoFromDb = manager.load(entityId);

        assertEquals(targetBookTitle, dtoFromDb.getTitle());
        assertEquals(targetBookIsbn10, dtoFromDb.getIsbn10());
        assertEquals(targetBookIsbn13, dtoFromDb.getIsbn13());
        assertEquals(unrelatedPrice, dtoFromDb.getPrice());
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

        assertThrows(BookNotProcessableException.class, () -> manager.save(givenDto));
        verify(itBookApi, never()).load(anyString());
    }

    @Test
    void ShouldNot_SaveBook_When_Isbn13DoesNotExist_And_Isbn10IsNotInvalid_And_OtherFieldsNotCompleted() {
        when(itBookApi.load(validButNotInItBookIsbn13)).thenThrow(BookNotFoundException.class);
        final BookDto givenDto = BookDto
                .builder()
                .isbn10(targetBookIsbn10)
                .isbn13(validButNotInItBookIsbn13)
                .build();

        assertThrows(BookNotFoundException.class, () -> manager.save(givenDto));
    }

    @Test
    void ShouldNot_LoadBook_When_BookWithSpecifiedLocationDoesntExist() {
        assertThrows(BookNotFoundException.class, () -> manager.load(Long.MAX_VALUE));
    }
}
