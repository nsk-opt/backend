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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.nskopt.dto.category.CategoryUpdateRequest;
import ru.nskopt.dto.category.CategoryUserResponse;
import ru.nskopt.dto.product.ProductUserResponse;
import ru.nskopt.services.CategoryService;
import ru.nskopt.utils.SecurityUtils;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "Управление категориями товаров")
public class CategoryController {

  private final CategoryService categoryService;
  private final SecurityUtils securityUtils;

  @GetMapping
  @Operation(summary = "Получить все категории")
  public ResponseEntity<?> getAllCategories(Authentication authentication) {
    if (securityUtils.hasManagerRole(authentication))
      return ResponseEntity.ok(categoryService.findAllAdmin());

    return ResponseEntity.ok(categoryService.findAll());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить категорию по ID")
  public ResponseEntity<?> getCategoryById(@PathVariable Long id, Authentication authentication) {
    if (securityUtils.hasManagerRole(authentication))
      return ResponseEntity.ok(categoryService.findByIdAdmin(id));

    return ResponseEntity.ok(categoryService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать новую категорию",
      description = "Создаёт новую категорию на основе переданных данных.")
  public CategoryUserResponse createCategory(
      @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
    return categoryService.save(categoryUpdateRequest);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Обновить категорию по ID",
      description = "Обновляет данные категории по её уникальному идентификатору.")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  public CategoryUserResponse updateCategory(
      @Parameter(description = "ID категории", example = "1") @PathVariable Long id,
      @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
    return categoryService.update(id, categoryUpdateRequest);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить категорию по ID",
      description = "Удаляет категорию по её уникальному идентификатору.")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  public void deleteCategory(
      @Parameter(description = "ID категории", example = "1") @PathVariable Long id) {
    categoryService.deleteById(id);
  }

  @PutMapping("/{categoryId}/images")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Обновить изображения категории",
      description = "Обновляет список изображений, связанных с категорией.")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  public void updateImages(
      @Parameter(description = "ID категории", example = "1") @PathVariable Long categoryId,
      @RequestBody List<Long> imageIds) {
    categoryService.updateImages(categoryId, imageIds);
  }

  @GetMapping("/{categoryId}/images")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary = "Получить ID изображений категории",
      description = "Возвращает список ID изображений, связанных с категорией.")
  public List<Long> getImagesId(
      @Parameter(description = "ID категории", example = "1") @PathVariable Long categoryId) {
    return categoryService.getImagesIds(categoryId);
  }

  @GetMapping("/{categoryId}/products")
  public List<ProductUserResponse> getProductsIdByCategoryId(@PathVariable Long categoryId) {
    return categoryService.getProductsByCategoryId(categoryId);
  }
}
