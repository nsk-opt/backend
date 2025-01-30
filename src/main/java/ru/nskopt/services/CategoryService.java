package ru.nskopt.services;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.nskopt.models.Category;
import ru.nskopt.repositories.CategoryRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Optional<Category> findById(Long id) {
    return categoryRepository.findById(id);
  }

  public Category save(Category category) {
    log.info("Save {}", category);
    return categoryRepository.save(category);
  }

  public void deleteById(Long id) {
    log.info("Delete category with id {}", id);
    categoryRepository.deleteById(id);
  }
}
