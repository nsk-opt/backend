package ru.nskopt.controllers;

import java.util.List;
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
import ru.nskopt.models.entities.Product;
import ru.nskopt.services.ProductService;

@RestController
@RequestMapping(value = "/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public List<Product> getAllProducts() {
    return productService.findAll();
  }

  @GetMapping("/{id}")
  public Product getProductById(@PathVariable Long id) {
    return productService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Product createProduct(@RequestBody Product product) {
    return productService.save(product);
  }

  @PutMapping("/{id}")
  public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {

    productService.findById(id);
    product.setId(id);

    return productService.save(product);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable Long id) {
    productService.deleteById(id);
  }

  @PostMapping("/{productId}/categories/{categoryId}")
  @ResponseStatus(HttpStatus.OK)
  public void addCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
    productService.addCategory(productId, categoryId);
  }

  @DeleteMapping("/{productId}/categories/{categoryId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
    productService.removeCategory(productId, categoryId);
  }
}
