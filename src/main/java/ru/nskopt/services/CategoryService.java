package ru.nskopt.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.models.entities.Category;
import ru.nskopt.repositories.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Category findById(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
  }

  public Category save(Category category) {
    log.info("Save {}", category);

    return categoryRepository.save(category);
  }

  public void deleteById(Long id) {
    if (!categoryRepository.existsById(id))
      throw new ResourceNotFoundException("Category with id " + id + " not found");

    log.info("Delete category with id {}", id);
    categoryRepository.deleteById(id);
  }
}
