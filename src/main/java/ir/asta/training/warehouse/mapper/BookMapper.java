package ir.asta.training.warehouse.mapper;

import ir.asta.training.warehouse.dto.ItBookApiDto;
import ir.asta.training.warehouse.dto.BookDto;
import ir.asta.training.warehouse.entity.BookEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BookMapper {

    public BookEntity toEntity(BookDto dto) {
        return BookEntity.builder()
                .title(dto.getTitle())
                .isbn10(dto.getIsbn10())
                .isbn13(dto.getIsbn13())
                .price(dto.getPrice())
                .build();
    }

    public BookEntity toEntity(ItBookApiDto dto) {
        return BookEntity.builder()
                .title(dto.getTitle())
                .isbn10(dto.getIsbn10())
                .isbn13(dto.getIsbn13())
                .price(BigDecimal.valueOf(Double.parseDouble(dto.getPrice().replace('$', ' '))))
                .build();
    }

    public BookDto toDto(BookEntity entity) {
        return BookDto.builder()
                .title(entity.getTitle())
                .isbn10(entity.getIsbn10())
                .isbn13(entity.getIsbn13())
                .price(entity.getPrice())
                .build();
    }
}
