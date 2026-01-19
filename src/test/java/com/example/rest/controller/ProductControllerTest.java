package com.example.rest.controller;

import com.example.rest.dto.ProductDto;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDto productDto1;
    private ProductDto productDto2;

    @BeforeEach
    void setUp() {
        productDto1 = new ProductDto(1L, "Laptop", 999.99);
        productDto2 = new ProductDto(2L, "Mouse", 29.99);
    }

    @Test
    void testGetAllProducts_Success() throws Exception {
        List<ProductDto> products = Arrays.asList(productDto1, productDto2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].name", is("Mouse")));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetAllProducts_EmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById_Success() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDto1);

        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(get("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Product not found with id: 999")));

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        ProductDto inputDto = new ProductDto(null, "Laptop", 999.99);
        when(productService.createProduct(any(ProductDto.class))).thenReturn(productDto1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productService, times(1)).createProduct(any(ProductDto.class));
    }

    @Test
    void testCreateProduct_ValidationError_BlankName() throws Exception {
        ProductDto invalidDto = new ProductDto(null, "", 999.99);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());

        verify(productService, never()).createProduct(any(ProductDto.class));
    }

    @Test
    void testCreateProduct_ValidationError_InvalidPrice() throws Exception {
        ProductDto invalidDto = new ProductDto(null, "Laptop", 0.0);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());

        verify(productService, never()).createProduct(any(ProductDto.class));
    }

    @Test
    void testCreateProduct_ValidationError_NullPrice() throws Exception {
        ProductDto invalidDto = new ProductDto(null, "Laptop", null);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());

        verify(productService, never()).createProduct(any(ProductDto.class));
    }

    @Test
    void testCreateProduct_DuplicateName() throws Exception {
        ProductDto inputDto = new ProductDto(null, "Laptop", 999.99);
        when(productService.createProduct(any(ProductDto.class)))
                .thenThrow(new DuplicateResourceException("Product with name Laptop already exists"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Product with name Laptop already exists")));

        verify(productService, times(1)).createProduct(any(ProductDto.class));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        ProductDto updatedDto = new ProductDto(1L, "Gaming Laptop", 1299.99);
        when(productService.updateProduct(eq(1L), any(ProductDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Gaming Laptop")))
                .andExpect(jsonPath("$.price", is(1299.99)));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductDto.class));
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        ProductDto updatedDto = new ProductDto(999L, "Laptop", 999.99);
        when(productService.updateProduct(eq(999L), any(ProductDto.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Product not found with id: 999")));

        verify(productService, times(1)).updateProduct(eq(999L), any(ProductDto.class));
    }

    @Test
    void testUpdateProduct_ValidationError() throws Exception {
        ProductDto invalidDto = new ProductDto(1L, "", 0.0);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        verify(productService, never()).updateProduct(eq(1L), any(ProductDto.class));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Product not found with id: 999")));

        verify(productService, times(1)).deleteProduct(999L);
    }
}
