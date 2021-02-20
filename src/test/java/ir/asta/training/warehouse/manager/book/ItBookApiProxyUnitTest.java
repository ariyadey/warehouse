package ir.asta.training.warehouse.manager.book;

import ir.asta.training.warehouse.dto.ItBookApiDto;
import ir.asta.training.warehouse.manager.book.exception.BookNotFoundException;
import ir.asta.training.warehouse.util.ReflectionUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItBookApiProxyUnitTest {
    private ItBookApiProxy api;

    @BeforeAll
    void init() {
        api = new ItBookApiProxy();
    }

    @Test
    void Should_LoadBook_When_BookWithTheIsbn13Exists() {
        final ItBookApiDto dto = api.load("9781449361341");

        assertNotNull(dto);
        assertFalse(ReflectionUtil.hasNullField(dto));
        assertEquals("0", dto.getError());
    }

    @Test
    void ShouldNot_LoadBook_When_BookWithTheIsbn13DoesNotExist() {
        assertThrows(BookNotFoundException.class, () -> api.load("9786227233797"));
    }
}
