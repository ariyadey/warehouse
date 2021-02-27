package ir.asta.training.warehouse.manager.category.exception;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 3094491455972760599L;
    private final String code;

    public CategoryNotFoundException(String code) {
        super(String.format("Category with the code: %s was not found", code));
        this.code = code;
    }
}
