package ir.asta.training.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor()
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItBookApiDto {
    private String error;
    private String title;
    private String isbn10;
    private String isbn13;
    private String  price;
}
