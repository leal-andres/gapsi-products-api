package mx.com.grupoasesores.products.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.com.grupoasesores.products.dto.ProductResponse;
import mx.com.grupoasesores.products.dto.ProductUpdateRequest;
import mx.com.grupoasesores.products.entity.Product;
import mx.com.grupoasesores.products.error.exceptions.InternalErrorException;
import mx.com.grupoasesores.products.error.exceptions.ProductNotFoundException;
import mx.com.grupoasesores.products.mapper.ProductMapper;
import mx.com.grupoasesores.products.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String PRODUCT_ID = "PRD0000001";
    private static final String MISSING_ID = "PRD9999999";

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Laptop");
        product.setDescription("Original description");
        product.setPrice(new BigDecimal("1999.99"));
        product.setModel("OLDMODEL01");

        productResponse = new ProductResponse();
        productResponse.setId(PRODUCT_ID);
        productResponse.setName("Laptop");
        productResponse.setDescription("Original description");
        productResponse.setPrice(new BigDecimal("1999.99"));
        productResponse.setModel("OLDMODEL01");
    }

    @Test
    void getProductById_whenFound_returnsMappedResponse() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.getProductById(PRODUCT_ID);

        assertThat(result).isEqualTo(productResponse);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProductById_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(MISSING_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(MISSING_ID);

        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void updateProduct_whenDescriptionProvided_updatesOnlyDescription() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("New description");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        productService.updateProduct(PRODUCT_ID, request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();
        assertThat(saved.getDescription()).isEqualTo("New description");
        assertThat(saved.getModel()).isEqualTo("OLDMODEL01");
    }

    @Test
    void updateProduct_whenModelProvided_updatesOnlyModel() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setModel("NEWMODEL01");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        productService.updateProduct(PRODUCT_ID, request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();
        assertThat(saved.getDescription()).isEqualTo("Original description");
        assertThat(saved.getModel()).isEqualTo("NEWMODEL01");
    }

    @Test
    void updateProduct_whenBothFieldsProvided_updatesBoth() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("New description");
        request.setModel("NEWMODEL01");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        productService.updateProduct(PRODUCT_ID, request);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();
        assertThat(saved.getDescription()).isEqualTo("New description");
        assertThat(saved.getModel()).isEqualTo("NEWMODEL01");
    }

    @Test
    void updateProduct_whenFound_returnsMappedResponse() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("New description");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.updateProduct(PRODUCT_ID, request);

        assertThat(result).isEqualTo(productResponse);
    }

    @Test
    void updateProduct_whenNotFound_throwsProductNotFoundException() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("New description");

        when(productRepository.findById(MISSING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(MISSING_ID, request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(MISSING_ID);

        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void updateProduct_whenSaveFails_throwsInternalErrorException() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("New description");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenThrow(new RuntimeException("DB failure"));

        assertThatThrownBy(() -> productService.updateProduct(PRODUCT_ID, request))
                .isInstanceOf(InternalErrorException.class)
                .hasMessageContaining(PRODUCT_ID);
    }
}
