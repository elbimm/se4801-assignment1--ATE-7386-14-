package com.shopwave.service;

import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.mapper.ProductMapper;
import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.repository.CategoryRepository;
import com.shopwave.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private CreateProductRequest request;
    private Category category;
    private Product product;
    private Product savedProduct;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        // 1. Setup Request
        request = new CreateProductRequest(
                "Test Laptop",
                "High performance laptop",
                new BigDecimal("1200.00"),
                10,
                1L);

        // 2. Setup Category Entity
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        // 3. Setup Product Entity (before save)
        product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        // 4. Setup Saved Product Entity (after save, has ID)
        savedProduct = Product.builder()
                .id(100L)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();

        // 5. Setup DTO
        productDTO = ProductDTO.builder()
                .id(100L)
                .name("Test Laptop")
                .price(new BigDecimal("1200.00"))
                .stock(10)
                .categoryId(1L)
                .categoryName("Electronics")
                .build();
    }

    @Test
    @DisplayName("createProduct - Happy Path: Should create product when category exists")
    void createProduct_HappyPath() {
        // Arrange: Mock behaviors
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(request, category)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.createProduct(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test Laptop", result.getName());
        assertEquals(new BigDecimal("1200.00"), result.getPrice());

        // Verify interactions
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(product);
        verify(productMapper).toEntity(request, category);
        verify(productMapper).toDTO(savedProduct);
    }

    @Test
    @DisplayName("createProduct - Error Path: Should throw IllegalArgumentException when category not found")
    void createProduct_CategoryNotFound() {
        // Arrange: Mock categoryRepository to return empty
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(request));

        assertEquals("Category not found with id: 1", exception.getMessage());

        // Verify save was NEVER called
        verify(productRepository, never()).save(any());
        verify(categoryRepository).findById(1L);
    }
}