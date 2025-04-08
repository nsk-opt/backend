package ru.nskopt.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nskopt.dto.category.CategoryUpdateRequest;
import ru.nskopt.dto.category.CategoryUserResponse;
import ru.nskopt.dto.product.ProductUserResponse;
import ru.nskopt.entities.Category;
import ru.nskopt.entities.image.Image;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.mappers.CategoryMapper;
import ru.nskopt.mappers.ProductMapper;
import ru.nskopt.repositories.CategoryRepository;
import ru.nskopt.repositories.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final ProductMapper productMapper;

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;
  private final ImageService imageService;
  private final CategoryMapper categoryMapper;

  @Transactional(readOnly = true)
  public List<CategoryUserResponse> findAll() {
    return categoryRepository.findAll().stream().map(categoryMapper::toUserResponse).toList();
  }

  @Transactional(readOnly = true)
  public CategoryUserResponse findById(Long id) {
    return categoryMapper.toUserResponse(
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found " + id)));
  }

  public CategoryUserResponse save(CategoryUpdateRequest categoryUpdateRequest) {
    log.info("Save {}", categoryUpdateRequest);

    return categoryMapper.toUserResponse(
        categoryRepository.save(categoryMapper.toCategory(categoryUpdateRequest)));
  }

  public CategoryUserResponse update(Long id, CategoryUpdateRequest categoryUpdateRequest) {
    Category existingCategory =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found " + id));
    categoryMapper.updateCategoryFromRequest(categoryUpdateRequest, existingCategory);

    log.info("Update {}", existingCategory);

    return categoryMapper.toUserResponse(categoryRepository.save(existingCategory));
  }

  public void deleteById(Long id) {
    if (!categoryRepository.existsById(id))
      throw new ResourceNotFoundException("Category not found " + id);

    log.info("Delete category with id {}", id);
    categoryRepository.deleteById(id);
  }

  @Transactional
  public void updateImages(Long categoryId, List<Long> imagesIds) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found " + categoryId));

    List<Image> images = imageService.getImagesByIds(imagesIds);

    category.getImages().clear();
    category.getImages().addAll(images);

    categoryRepository.save(category);

    log.info("Updated images for category ID {}: {}", categoryId, imagesIds);
  }

  @Transactional(readOnly = true)
  public List<Long> getImagesIds(Long categoryId) {
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found " + categoryId));

    return category.getImages().stream().map(Image::getId).toList();
  }

  public List<ProductUserResponse> getProductsByCategoryId(Long categoryId) {
    return productRepository.findAllProductsIdByCategoryId(categoryId).stream().map(productMapper::toUserResponse).toList();
  }
}
