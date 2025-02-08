package ru.nskopt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Product;
import ru.nskopt.models.requests.UpdateProductRequest;
import ru.nskopt.services.ProductService;

@RestController
@RequestMapping(value = "/api/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product controller")
public class ProductController {

  private final ProductService productService;

  @GetMapping
  @Operation(summary = "Get all products")
  public List<Product> getAllProducts() {
    return productService.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get product by id")
  public Product getProductById(@PathVariable Long id) {
    return productService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create product")
  public Product createProduct(@RequestBody UpdateProductRequest updateProductRequest) {
    return productService.save(updateProductRequest);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update product by id")
  public Product updateProduct(
      @PathVariable Long id, @RequestBody UpdateProductRequest updateProductRequest) {
    return productService.update(id, updateProductRequest);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete product by id")
  public void deleteProduct(@PathVariable Long id) {
    productService.deleteById(id);
  }

  @GetMapping("/{productId}/categories")
  public Set<Category> getCategories(@PathVariable Long productId) {
    return productService.getCategoriesByProductId(productId);
  }

  @PostMapping("/{productId}/categories/{categoryId}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Add category to product by id")
  public void addCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
    productService.addCategory(productId, categoryId);
  }

  @DeleteMapping("/{productId}/categories/{categoryId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Remove category to product by id")
  public void removeCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
    productService.removeCategory(productId, categoryId);
  }
}
