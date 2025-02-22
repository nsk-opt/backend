package ru.nskopt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import ru.nskopt.models.requests.UpdateProductRequest;
import ru.nskopt.services.ProductService;

@RestController
@RequestMapping(value = "/api/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Controller", description = "Управление товарами")
public class ProductController {

  private final ProductService productService;

  @GetMapping
  @Operation(
      summary = "Получить все товары",
      description = "Возвращает список всех доступных товаров.")
  public List<Product> getAllProducts() {
    return productService.findAll();
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Получить товар по ID",
      description = "Возвращает товар по его уникальному идентификатору.")
  public Product getProductById(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long id) {
    return productService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать новый товар",
      description = "Создаёт новый товар на основе переданных данных.")
  public Product createProduct(@Valid @RequestBody UpdateProductRequest updateProductRequest) {
    return productService.save(updateProductRequest);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Обновить товар по ID",
      description = "Обновляет данные товара по его уникальному идентификатору.")
  public Product updateProduct(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long id,
      @Valid @RequestBody UpdateProductRequest updateProductRequest) {
    return productService.update(id, updateProductRequest);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить товар по ID",
      description = "Удаляет товар по его уникальному идентификатору.")
  public void deleteProduct(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long id) {
    productService.deleteById(id);
  }

  @PutMapping("/{productId}/categories")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Обновить категории для товара",
      description = "Обновляет список категорий для товара по его уникальному идентификатору.")
  public void updateCategories(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long productId,
      @RequestBody List<Long> categoryIds) {
    productService.updateCategories(productId, categoryIds);
  }
}
