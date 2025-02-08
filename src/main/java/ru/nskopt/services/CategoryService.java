package ru.nskopt.services;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.mappers.Mapper;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Product;
import ru.nskopt.models.requests.UpdateCategoryRequest;
import ru.nskopt.repositories.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final Mapper<Category, UpdateCategoryRequest> categoryMapper;

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Category findById(Long id) {
    return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
  }

  public Category save(UpdateCategoryRequest updateCategoryRequest) {
    log.info("Save {}", updateCategoryRequest);

    return categoryRepository.save(categoryMapper.map(updateCategoryRequest));
  }

  public Category update(Long id, UpdateCategoryRequest updateCategoryRequest) {
    return categoryRepository.findById(id).map(existingCategory -> {
      categoryMapper.update(existingCategory, updateCategoryRequest);

      log.info("Update {}", existingCategory);

      return categoryRepository.save(existingCategory);
    }).orElseThrow(() -> new ResourceNotFoundException(id));
  }

  public void deleteById(Long id) {
    if (!categoryRepository.existsById(id))
      throw new ResourceNotFoundException(id);

    log.info("Delete category with id {}", id);
    categoryRepository.deleteById(id);
  }

  public Set<Product> getProductsByCategoryId(Long id) {
    if (!categoryRepository.existsById(id))
      throw new ResourceNotFoundException(id);

    return categoryRepository.findProductsByCategoryId(id);
  }
}
