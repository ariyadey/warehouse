package ir.asta.training.warehouse.manager.book;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IsbnValidatorUnitTest {
    private IsbnValidator validator;

    @BeforeAll
    void init() {
        validator = new IsbnValidator();
    }

    @Test
    void Should_Validate_When_Isbn10IsValid() {
        assertTrue(validator.isIsbn10Valid("144936134X"));
    }

    @Test
    void ShouldNot_Validate_When_Isbn10IsInvalid() {
        assertFalse(validator.isIsbn10Valid("1449361340"));
    }

    @Test
    void Should_Validate_When_Isbn13IsValid() {
        assertTrue(validator.isIsbn13Valid("9781449361341"));
    }

    @Test
    void ShouldNot_Validate_When_Isbn13IsInvalid() {
        assertFalse(validator.isIsbn13Valid("9781449361340"));
    }
}
