package ru.nskopt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import ru.nskopt.dto.product.ProductUpdateRequest;
import ru.nskopt.dto.product.ProductUserResponse;
import ru.nskopt.services.ProductService;
import ru.nskopt.utils.SecurityUtils;

@RestController
@RequestMapping(value = "/api/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Controller", description = "Управление товарами")
public class ProductController {

  private final ProductService productService;
  private final SecurityUtils securityUtils;

  @GetMapping
  @Operation(summary = "Получить все продукты")
  public ResponseEntity<?> getAllCategories(Authentication authentication) {
    if (securityUtils.hasManagerRole(authentication))
      return ResponseEntity.ok(productService.findAllAdmin());

    return ResponseEntity.ok(productService.findAll());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить продукт по ID")
  public ResponseEntity<?> getCategoryById(@PathVariable Long id, Authentication authentication) {
    if (securityUtils.hasManagerRole(authentication))
      return ResponseEntity.ok(productService.findByIdAdmin(id));

    return ResponseEntity.ok(productService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать новый товар",
      description = "Создаёт новый товар на основе переданных данных.")
  public ProductUserResponse createProduct(
      @Valid @RequestBody ProductUpdateRequest updateProductRequest) {
    return productService.save(updateProductRequest);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  @Operation(
      summary = "Обновить товар по ID",
      description = "Обновляет данные товара по его уникальному идентификатору.")
  public ProductUserResponse updateProduct(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long id,
      @Valid @RequestBody ProductUpdateRequest updateProductRequest) {
    return productService.update(id, updateProductRequest);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить товар по ID",
      description = "Удаляет товар по его уникальному идентификатору.")
  public void deleteProduct(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long id) {
    productService.deleteById(id);
  }

  @PutMapping("/{productId}/categories")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Обновить категории для товара",
      description = "Обновляет список категорий для товара по его уникальному идентификатору.")
  public void updateCategories(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long productId,
      @RequestBody List<Long> categoryIds) {
    productService.updateCategories(productId, categoryIds);
  }

  @PutMapping("/{productId}/images")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Обновить изображения товара",
      description = "Обновляет список изображений для товара по его уникальному идентификатору.")
  public void updateImages(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long productId,
      @RequestBody List<Long> imageIds) {
    productService.updateImages(productId, imageIds);
  }

  @GetMapping("/{productId}/images")
  @Operation(
      summary = "Получить ID изображений товара",
      description = "Возвращает список ID изображений, связанных с товаром.")
  public List<Long> getImagesIds(
      @Parameter(description = "ID товара", example = "1") @PathVariable Long productId) {
    return productService.getImagesIds(productId);
  }
}
