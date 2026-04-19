package mx.com.grupoasesores.products.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequest {

    @Size(max = 200, message = "Description must be between 1 and 200 characters")
    private String description;

    @Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "Model must be 10 characters")
    private String model;

    @AssertTrue(message = "At least one field (description, model) must be provided")
    public boolean isAtLeastOneFieldPresent() {
        return description != null || model != null;
    }
}
