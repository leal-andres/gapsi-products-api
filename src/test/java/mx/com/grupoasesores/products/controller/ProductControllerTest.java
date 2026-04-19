package mx.com.grupoasesores.products.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.com.grupoasesores.products.dto.ProductResponse;
import mx.com.grupoasesores.products.dto.ProductUpdateRequest;
import mx.com.grupoasesores.products.error.exceptions.InternalErrorException;
import mx.com.grupoasesores.products.error.exceptions.ProductNotFoundException;
import mx.com.grupoasesores.products.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final String VALID_ID = "PRD0000001";
    private static final String INVALID_ID = "SHORT";
    private static final String BASE_URL = "/api/v1/products";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponse();
        productResponse.setId(VALID_ID);
        productResponse.setName("Laptop");
        productResponse.setDescription("High-end laptop");
        productResponse.setPrice(new BigDecimal("1999.99"));
        productResponse.setModel("MODEL00001");
    }

    @Test
    void getProductById_whenIdValid_returnsOk() throws Exception {
        when(productService.getProductById(VALID_ID)).thenReturn(productResponse);

        mockMvc.perform(get(BASE_URL + "/{id}", VALID_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.model").value("MODEL00001"));
    }

    @Test
    void getProductById_whenIdInvalid_returnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", INVALID_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void getProductById_whenProductNotFound_returnsNotFound() throws Exception {
        when(productService.getProductById(VALID_ID))
                .thenThrow(new ProductNotFoundException("Product not found with id: " + VALID_ID));

        mockMvc.perform(get(BASE_URL + "/{id}", VALID_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    void updateProduct_whenRequestValid_returnsOk() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("Updated description");
        request.setModel("NEWMODEL01");

        when(productService.updateProduct(eq(VALID_ID), any(ProductUpdateRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(patch(BASE_URL + "/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID));
    }

    @Test
    void updateProduct_whenIdInvalid_returnsBadRequest() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("Updated description");

        mockMvc.perform(patch(BASE_URL + "/{id}", INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void updateProduct_whenBodyEmpty_returnsBadRequest() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void updateProduct_whenDescriptionExceedsMax_returnsBadRequest() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("a".repeat(201));

        mockMvc.perform(patch(BASE_URL + "/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void updateProduct_whenModelInvalid_returnsBadRequest() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setModel("BAD");

        mockMvc.perform(patch(BASE_URL + "/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void updateProduct_whenBodyMalformed_returnsBadRequest() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void updateProduct_whenProductNotFound_returnsNotFound() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("Updated description");

        when(productService.updateProduct(eq(VALID_ID), any(ProductUpdateRequest.class)))
                .thenThrow(new ProductNotFoundException("Product not found with id: " + VALID_ID));

        mockMvc.perform(patch(BASE_URL + "/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    void updateProduct_whenInternalError_returnsInternalServerError() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setDescription("Updated description");

        when(productService.updateProduct(eq(VALID_ID), any(ProductUpdateRequest.class)))
                .thenThrow(new InternalErrorException("Error al actualizar el producto con id: " + VALID_ID));

        mockMvc.perform(patch(BASE_URL + "/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }
}
