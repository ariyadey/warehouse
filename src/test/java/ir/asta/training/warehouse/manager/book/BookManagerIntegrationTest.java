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
    private final String mockTitle = "This is a fake title for testing purposes";
    private final String itBookExistingIsbn13 = "9781449361341";
    private final String itBookNonExistingIsbn13 = "9786227233797";
    private final String validIsbn10 = "144936134X";
    private final String invalidIsbn10 = "1449361340";
    private final String validIsbn13 = itBookNonExistingIsbn13;
    private final String invalidIsbn13 = "9781449361340";
    private final BigDecimal originalPrice = BigDecimal.valueOf(22.00);
    private final BigDecimal mockPrice = BigDecimal.valueOf(1.000);

    @Autowired
    private BookManager manager;

    @MockBean
    private ItBookApiProxy itBookApiProxy;

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

        assertThrows(BookNotProcessableException.class, () -> manager.save(dto));
        verify(itBookApiProxy, never()).load(anyString());
    }

    @Test
    void ShouldNot_SaveBook_When_Isbn13DoesNotExist_And_Isbn10IsNotInvalid_And_OtherFieldsNotCompleted() {
        when(itBookApiProxy.load(anyString())).thenThrow(BookNotFoundException.class);
        final BookDto dto = BookDto
                .builder()
                .isbn10(validIsbn10)
                .isbn13(itBookNonExistingIsbn13)
                .build();

        assertThrows(BookNotFoundException.class, () -> manager.save(dto));
    }

    private void assertSaves(BookDto dto) {
        when(itBookApiProxy.load(anyString())).thenReturn(ItBookApiDto.builder()
                .title(originalTitle)
                .isbn10(validIsbn10)
                .isbn13(itBookExistingIsbn13)
                .price(String.valueOf(originalPrice))
                .build());

        final long entityId = manager.save(dto);


        final BookDto expectedDto = BookDto.builder()
                .title(dto.getTitle() == null ? originalTitle : dto.getTitle())
                .isbn10(dto.getIsbn10() == null ? validIsbn10 : dto.getIsbn10())
                .isbn13(dto.getIsbn13() == null ? itBookExistingIsbn13 : dto.getIsbn13())
                .price(dto.getPrice() == null ? originalPrice : dto.getPrice())
                .build();
        final BookDto actualDto = manager.load(entityId);

        assertEquals(expectedDto, actualDto);
    }
}
