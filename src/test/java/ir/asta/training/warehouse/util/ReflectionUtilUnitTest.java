package ir.asta.training.warehouse.util;

import ir.asta.training.warehouse.dto.BookDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static ir.asta.training.warehouse.util.ReflectionUtil.containsField;
import static ir.asta.training.warehouse.util.ReflectionUtil.hasNullField;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReflectionUtilUnitTest {

    @Test
    void Should_ReturnTrue_When_ObjectHasNoNullField() {
        BookDto dto = BookDto.builder()
                .title("A title")
                .isbn10("4567984359")
                .isbn13("4798461305460")
                .price(BigDecimal.ZERO)
                .build();

        assertFalse(hasNullField(dto));
    }

    @Test
    void Should_ReturnFalse_When_ObjectHasNullField() {
        BookDto dto = BookDto.builder()
                .title("A title")
                .isbn10("4567984359")
                .isbn13("4798461305460")
                .build();

        assertTrue(hasNullField(dto));
    }

    @Test
    void Should_ReturnTrue_When_ObjectContainsField() {
        Class<?> givenClass = BookDto.class;
        String givenFieldName = "title";

        assertTrue(containsField(givenClass, givenFieldName));
    }

    @Test
    void Should_ReturnFalse_When_ObjectDoesntContainField() {
        Class<?> givenClass = BookDto.class;
        String givenFieldName = "TITLE";

        assertFalse(containsField(givenClass, givenFieldName));
    }
}
