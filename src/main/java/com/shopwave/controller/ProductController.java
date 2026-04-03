package com.shopwave.controller;

import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.service.ProductService;
import jakarta.validation.Valid; // we add the dependency of the pom.xml
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products?page=0&size=10
    // Returns paginated list of products.

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    // To Returns a single product or 404 if not found.

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // POST
    // Creates a new product. Returns 201 Created with location header.

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDTO created = productService.createProduct(request);
        URI location = URI.create("/api/products/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    // Searches products by optional keyword and max price.

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(productService.searchProducts(keyword, maxPrice));
    }

    // Updates stock by a delta (positive or negative).

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable Long id,
            @RequestBody StockDeltaRequest request) {
        return ResponseEntity.ok(productService.updateStock(id, request.getDelta()));
    }

    // Inner class for stock update request body
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StockDeltaRequest {
        private int delta;
    }
}