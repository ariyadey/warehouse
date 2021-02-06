package ir.asta.training.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String isbn10;
    private String isbn13;
    private BigDecimal price;
    private String title;
}
