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

    private final String originalTitle = "RESTful Java with JAX-RS 2.0, 2nd Edition";
    private final String fakeTitle = "This is a fake title for testing purposes";
    private final String itBookExistingIsbn13 = "9781449361341";
    private final String itBookNonExistingIsbn13 = "9786227233797";
    private final String validIsbn10 = "144936134X";
    private final String invalidIsbn10 = "1449361340";
    private final String validIsbn13 = itBookNonExistingIsbn13;
    private final String invalidIsbn13 = "9781449361340";
    private final BigDecimal originalPrice = BigDecimal.valueOf(22.00);
    private final BigDecimal fakePrice = BigDecimal.valueOf(1.000);

    @Autowired
    private BookManager manager;

    @MockBean
    private ItBookApiProxy itBookApiProxy;

    private ItBookApiDto getOriginalDto() {
        return ItBookApiDto.builder()
                .title(originalTitle)
                .isbn10(validIsbn10)
                .isbn13(itBookExistingIsbn13)
                .price(String.valueOf(originalPrice))
                .build();
    }

    @Test
    void Should_SaveBook_When_BothIsbnsAreValid_And_OtherFieldsCompleted() {
        final BookDto givenDto = BookDto
                .builder()
                .title(fakeTitle)
                .isbn10(validIsbn10)
                .isbn13(validIsbn13)
                .price(fakePrice)
                .build();

        final long entityId = manager.save(givenDto);
        final BookDto loadedDto = manager.load(entityId);

        assertEquals(fakeTitle, loadedDto.getTitle());
        assertEquals(validIsbn10, loadedDto.getIsbn10());
        assertEquals(validIsbn13, loadedDto.getIsbn13());
        assertEquals(fakePrice, loadedDto.getPrice());
        verify(itBookApiProxy, never()).load(anyString());
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsValid_And_OtherFieldsNotCompleted() {
        when(itBookApiProxy.load(itBookExistingIsbn13)).thenReturn(getOriginalDto());
        final BookDto givenDto = BookDto
                .builder()
                .isbn10(validIsbn10)
                .isbn13(itBookExistingIsbn13)
                .build();

        final long entityId = manager.save(givenDto);
        final BookDto dtoFromDb = manager.load(entityId);

        assertEquals(originalTitle, dtoFromDb.getTitle());
        assertEquals(validIsbn10, dtoFromDb.getIsbn10());
        assertEquals(itBookExistingIsbn13, dtoFromDb.getIsbn13());
        assertEquals(originalPrice, dtoFromDb.getPrice());
    }

    @Test
    void Should_SaveBook_When_Isbn13Exists_And_Isbn10IsNotGiven() {
        when(itBookApiProxy.load(itBookExistingIsbn13)).thenReturn(getOriginalDto());
        final BookDto givenDto = BookDto
                .builder()
                .isbn13(itBookExistingIsbn13)
                .price(fakePrice)
                .build();

        final long entityId = manager.save(givenDto);
        final BookDto dtoFromDb = manager.load(entityId);

        assertEquals(originalTitle, dtoFromDb.getTitle());
        assertEquals(validIsbn10, dtoFromDb.getIsbn10());
        assertEquals(itBookExistingIsbn13, dtoFromDb.getIsbn13());
        assertEquals(fakePrice, dtoFromDb.getPrice());
    }

    @Test
    void ShouldNot_SaveBook_When_OneOfIsbnsAreInvalid() {
        final BookDto givenDto = BookDto
                .builder()
                .title(fakeTitle)
                .isbn10(validIsbn10)
                .isbn13(invalidIsbn13)
                .price(fakePrice)
                .build();

        assertThrows(BookNotProcessableException.class, () -> manager.save(givenDto));
        verify(itBookApiProxy, never()).load(anyString());
    }

    @Test
    void ShouldNot_SaveBook_When_Isbn13DoesNotExist_And_Isbn10IsNotInvalid_And_OtherFieldsNotCompleted() {
        when(itBookApiProxy.load(itBookNonExistingIsbn13)).thenThrow(BookNotFoundException.class);
        final BookDto givenDto = BookDto
                .builder()
                .isbn10(validIsbn10)
                .isbn13(itBookNonExistingIsbn13)
                .build();

        assertThrows(BookNotFoundException.class, () -> manager.save(givenDto));
    }
}
