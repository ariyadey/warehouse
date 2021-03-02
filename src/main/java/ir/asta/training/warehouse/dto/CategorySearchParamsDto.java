package ir.asta.training.warehouse.dto;

import ir.asta.training.warehouse.manager.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CategorySearchParamsDto {

    private String subject;

    private String code;

    @Value("${warehouse.search.default-page-size}")
    private Integer pageSize;

    @Value("${warehouse.search.default-page-number}")
    private Integer pageNumber;

    private String orderBy;

    private SortDirection sortDirection;
}
