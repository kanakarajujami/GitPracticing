package com.nt.controller;

import com.nt.CustomStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.nt.entity.Product;
import com.nt.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Product API", description = "CRUD operations for products")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponse(responseCode = "200", description = "List of products returned successfully")
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get product by ID", description = "Returns a product by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
        @Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new product", description = "Creates a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        return ResponseEntity.status(Integer.parseInt(CustomStatus.SUCCESS_STATUS))
                .body(CustomStatus.SUCCESS_MESSAGE);
    }

    @Operation(summary = "Update a product", description = "Updates an existing product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
        @Parameter(description = "ID of the product to update") @PathVariable Long id,
        @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a product", description = "Deletes a product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "ID of the product to delete") @PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Search products by description", description = "Returns products whose description contains the given keyword")
    @ApiResponse(responseCode = "200", description = "List of matching products returned successfully")
    @GetMapping("/search")
    public List<Product> searchProductsByDescription(@RequestParam("desc") String keyword) {
        return productService.searchProductsByDescription(keyword);
    }

    @Operation(summary = "Count products by price range", description = "Returns the count of products within the given price range")
    @ApiResponse(responseCode = "200", description = "Count returned successfully")
    @GetMapping("/count")
    public long countProductsByPriceRange(@RequestParam("start") Double start, @RequestParam("end") Double end) {
        return productService.countProductsByPriceRange(start, end);
    }
}
