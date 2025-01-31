package ru.nskopt.controllers;

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
import ru.nskopt.models.entities.Category;
import ru.nskopt.services.CategoryService;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public List<Category> getAllCategories() {
    return categoryService.findAll();
  }

  @GetMapping("/{id}")
  public Category getCategoryById(@Valid @PathVariable Long id) {
    return categoryService.findById(id);
  }

  @PostMapping
  public Category createCategory(@Valid @RequestBody Category category) {
    return categoryService.save(category);
  }

  @PutMapping("/{id}")
  public Category updateCategory(
      @PathVariable Long id, @Valid @RequestBody Category updatedCategory) {

    categoryService.findById(id);
    updatedCategory.setId(id);

    return categoryService.save(updatedCategory);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void deleteCategory(@PathVariable Long id) {
    categoryService.deleteById(id);
  }
}
