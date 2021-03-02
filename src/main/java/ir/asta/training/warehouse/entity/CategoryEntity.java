package ir.asta.training.warehouse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "WH_CATEGORY")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true, updatable = false)
    @NotBlank
    private String code;

    @Column(nullable = false)
    @NotBlank
    private String subject;

    public CategoryEntity(String code, String subject) {
        this.code = code;
        this.subject = subject;
    }
}
