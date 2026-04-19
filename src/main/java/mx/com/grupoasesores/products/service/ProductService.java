package mx.com.grupoasesores.products.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mx.com.grupoasesores.products.dto.ProductResponse;
import mx.com.grupoasesores.products.dto.ProductUpdateRequest;
import mx.com.grupoasesores.products.entity.Product;
import mx.com.grupoasesores.products.error.exceptions.ProductNotFoundException;
import mx.com.grupoasesores.products.mapper.ProductMapper;
import mx.com.grupoasesores.products.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse getProductById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public ProductResponse updateProduct(String id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (request.getDescription() != null)
            product.setDescription(request.getDescription());

        if (request.getModel() != null)
            product.setModel(request.getModel());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }
}
