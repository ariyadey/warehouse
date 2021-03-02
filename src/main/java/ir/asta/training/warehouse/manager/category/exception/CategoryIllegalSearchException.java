package ir.asta.training.warehouse.manager.category.exception;

import ir.asta.training.warehouse.dto.CategorySearchParamsDto;
import lombok.Getter;

@Getter
public class CategoryIllegalSearchException extends RuntimeException {
    private static final long serialVersionUID = -3836016846469248289L;
    private final transient CategorySearchParamsDto searchParams;

    public CategoryIllegalSearchException(CategorySearchParamsDto dto) {
        super(String.format("One or more search parameters are illegal: %s", dto));
        searchParams = dto;
    }
}
