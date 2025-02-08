package ru.nskopt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import ru.nskopt.models.requests.UpdateCategoryRequest;
import ru.nskopt.services.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Category controller")
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  @Operation(summary = "Get all categories")
  public List<Category> getAllCategories() {
    return categoryService.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get category by id")
  public Category getCategoryById(@Valid @PathVariable Long id) {
    return categoryService.findById(id);
  }

  @PostMapping
  @Operation(summary = "Create category")
  public Category createCategory(@Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
    return categoryService.save(updateCategoryRequest);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update category by id")
  public Category updateCategory(
      @PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
    return categoryService.update(id, updateCategoryRequest);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete category by id")
  public void deleteCategory(@PathVariable Long id) {
    categoryService.deleteById(id);
  }

  @GetMapping("/{id}/products")
  public Set<Product> getProductsByProductId(@PathVariable Long id) {
    return categoryService.getProductsByCategoryId(id);
  }
}
