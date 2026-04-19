package mx.com.grupoasesores.products.error.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private ErrorCode code;
    private String message;
    private Integer status;
    private String timestamp;
    private String path;
    private List<ErrorDetail> errors;
}
