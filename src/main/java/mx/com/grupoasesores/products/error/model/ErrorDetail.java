package mx.com.grupoasesores.products.error.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorDetail {
    private String field;
    private String message;
}
