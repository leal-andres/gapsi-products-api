package mx.com.grupoasesores.products.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import mx.com.grupoasesores.products.dto.ProductResponse;
import mx.com.grupoasesores.products.dto.ProductUpdateRequest;
import mx.com.grupoasesores.products.service.ProductService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable @Pattern(regexp = "^[A-Za-z0-9]{10}$") String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable @Pattern(regexp = "^[A-Za-z0-9]{10}$") String id,
                                                         @RequestBody @Valid ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
}
