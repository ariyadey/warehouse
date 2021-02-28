package ir.asta.training.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "WH_BOOK")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String isbn10;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String isbn13;

    @Column(nullable = false)
    @NotNull
    @Positive
    private BigDecimal price;
}
