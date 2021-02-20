package ir.asta.training.warehouse.manager.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

import static java.lang.Character.*;

@Slf4j
@Component
public class IsbnValidator {
    private enum IsbnType {
        ISBN10,
        ISBN13
    }

    public boolean isIsbn10Valid(String isbn) {
        boolean result = false;
        final char checkDigit = isbn.charAt(isbn.length() - 1);
        if (isIsbnFormatted(isbn, IsbnType.ISBN10) && (isDigit(checkDigit) || checkDigit == 'X')) {
            int sumOfDigits = IntStream
                    .range(0, 9)
                    .map(i -> getNumericValue(isbn.charAt(i)) * (10 - i))
                    .sum();
            sumOfDigits += (checkDigit == 'X') ? 10 : getNumericValue(checkDigit);
            result = sumOfDigits % 11 == 0;
        }
        return result;
    }

    public boolean isIsbn13Valid(String isbn) {
        boolean result = false;
        if (isIsbnFormatted(isbn, IsbnType.ISBN13)) {
            final int sumOfDigits = IntStream
                    .range(0, isbn.length())
                    .map(i -> getNumericValue(isbn.charAt(i)) * (i % 2 == 0 ? 1 : 3))
                    .sum();
            result = sumOfDigits % 10 == 0;
        }
        return result;
    }

    private boolean isIsbnFormatted(String isbn, IsbnType type) {
        boolean result = true;
        final int properLength = (type == IsbnType.ISBN10) ? 10 : 13;

        if (isbn == null || isbn.length() != properLength) {
            result = false;
        } else {
            for (int i = 0; i < properLength - 1; i++) {
                if (!isDigit(isbn.charAt(i))) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}
