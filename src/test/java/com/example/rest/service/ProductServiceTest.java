package com.example.rest.service;

import com.example.rest.dto.ProductDto;
import com.example.rest.entity.Product;
import com.example.rest.exception.DuplicateResourceException;
import com.example.rest.exception.ResourceNotFoundException;
import com.example.rest.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private ProductDto productDto1;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 999.99);
        product2 = new Product(2L, "Mouse", 29.99);
        productDto1 = new ProductDto(null, "Laptop", 999.99);
    }

    @Test
    void testGetAllProducts_Success() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(0, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        ProductDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(999.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });

        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateProduct_Success() {
        when(productRepository.existsByName("Laptop")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        ProductDto result = productService.createProduct(productDto1);

        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("Laptop", result.getName());
        assertEquals(999.99, result.getPrice());
        verify(productRepository, times(1)).existsByName("Laptop");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_DuplicateName() {
        when(productRepository.existsByName("Laptop")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            productService.createProduct(productDto1);
        });

        verify(productRepository, times(1)).existsByName("Laptop");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        ProductDto updateDto = new ProductDto(1L, "Gaming Laptop", 1299.99);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.existsByName("Gaming Laptop")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        ProductDto result = productService.updateProduct(1L, updateDto);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        ProductDto updateDto = new ProductDto(999L, "Laptop", 999.99);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(999L, updateDto);
        });

        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_DuplicateName() {
        ProductDto updateDto = new ProductDto(1L, "Mouse", 999.99);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.existsByName("Mouse")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            productService.updateProduct(1L, updateDto);
        });

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).existsByName("Mouse");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_SameName() {
        ProductDto updateDto = new ProductDto(1L, "Laptop", 799.99);
        Product updatedProduct = new Product(1L, "Laptop", 799.99);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductDto result = productService.updateProduct(1L, updateDto);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(799.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).existsByName(anyString());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });

        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(999L);
    }
}
