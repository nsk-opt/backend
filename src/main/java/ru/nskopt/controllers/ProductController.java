package ru.nskopt.controllers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nskopt.models.Category;
import ru.nskopt.models.Product;
import ru.nskopt.services.ProductService;

@RestController
@RequestMapping(value = "/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public ResponseEntity<List<Product>> getAllProducts() {
    List<Product> products = productService.findAll();
    return new ResponseEntity<>(products, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    return productService
        .findById(id)
        .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping
  public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product savedProduct = productService.save(product);
    return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(
      @PathVariable Long id, @RequestBody Product product) {
    if (productService.findById(id).isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    product.setId(id);
    Product updatedProduct = productService.save(product);

    return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    if (productService.findById(id).isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    productService.deleteById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/{productId}/categories")
  public ResponseEntity<Void> addCategoryToProduct(
      @PathVariable Long productId, @RequestBody Category category) {
    productService.addCategoryToProduct(productId, category);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{productId}/categories/{categoryId}")
  public ResponseEntity<Void> removeCategoryFromProduct(
      @PathVariable Long productId, @PathVariable Long categoryId) {
    productService.removeCategoryFromProduct(productId, categoryId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
