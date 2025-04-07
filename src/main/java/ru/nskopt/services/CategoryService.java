package ru.nskopt.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nskopt.entities.Category;
import ru.nskopt.entities.image.Image;
import ru.nskopt.entities.requests.UpdateCategoryRequest;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.mappers.Mapper;
import ru.nskopt.repositories.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final ImageService imageService;
  private final Mapper<Category, UpdateCategoryRequest> categoryMapper;

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Category findById(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found " + id));
  }

  public Category save(UpdateCategoryRequest updateCategoryRequest) {
    log.info("Save {}", updateCategoryRequest);

    return categoryRepository.save(categoryMapper.map(updateCategoryRequest));
  }

  public Category update(Long id, UpdateCategoryRequest updateCategoryRequest) {
    Category existingCategory = findById(id);
    categoryMapper.update(existingCategory, updateCategoryRequest);

    log.info("Update {}", existingCategory);

    return categoryRepository.save(existingCategory);
  }

  public void deleteById(Long id) {
    if (!categoryRepository.existsById(id))
      throw new ResourceNotFoundException("Category not found " + id);

    log.info("Delete category with id {}", id);
    categoryRepository.deleteById(id);
  }

  @Transactional
  public void updateImages(Long categoryId, List<Long> imagesIds) {
    Category category = findById(categoryId);

    List<Image> images = imageService.getImagesByIds(imagesIds);

    category.getImages().clear();
    category.getImages().addAll(images);

    categoryRepository.save(category);

    log.info("Updated images for category ID {}: {}", categoryId, imagesIds);
  }

  @Transactional
  public List<Long> getImagesIds(Long productId) {
    Category category = findById(productId);

    return category.getImages().stream().map(Image::getId).toList();
  }
}
