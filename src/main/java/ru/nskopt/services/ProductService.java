package ru.nskopt.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nskopt.dto.product.ProductUpdateRequest;
import ru.nskopt.dto.product.ProductUserResponse;
import ru.nskopt.entities.Category;
import ru.nskopt.entities.Product;
import ru.nskopt.entities.image.Image;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.mappers.ProductMapper;
import ru.nskopt.repositories.CategoryRepository;
import ru.nskopt.repositories.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  private final ImageService imageService;
  private final ProductMapper productMapper;

  @Transactional(readOnly = true)
  public List<ProductUserResponse> findAll() {
    return productRepository.findAll().stream().map(productMapper::toUserResponse).toList();
  }

  @Transactional(readOnly = true)
  public ProductUserResponse findById(Long id) {
    return productMapper.toUserResponse(
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found " + id)));
  }

  public ProductUserResponse save(ProductUpdateRequest request) {
    log.info("Save {}", request);
    return productMapper.toUserResponse(productRepository.save(productMapper.toProduct(request)));
  }

  public ProductUserResponse update(Long id, ProductUpdateRequest updateProductRequest) {
    Product existingProduct =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found " + id));
    productMapper.updateProductFromRequest(updateProductRequest, existingProduct);
    log.info("Updating product with ID {}: {}", id, existingProduct);
    return productMapper.toUserResponse(productRepository.save(existingProduct));
  }

  public void deleteById(Long id) {
    if (!productRepository.existsById(id))
      throw new ResourceNotFoundException("Product not found " + id);
    log.info("Delete product with id {}", id);
    productRepository.deleteById(id);
  }

  @Transactional
  public void updateCategories(Long productId, List<Long> categoryIds) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found " + productId));

    product.getCategories().clear();

    if (categoryIds.isEmpty()) log.info("Removed all categories for product ID {}", productId);

    Set<Category> categories =
        categoryIds.stream()
            .map(
                categoryId ->
                    categoryRepository
                        .findById(categoryId)
                        .orElseThrow(
                            () ->
                                new ResourceNotFoundException(
                                    "Category with id " + categoryId + " not found")))
            .collect(Collectors.toSet());
    product.getCategories().addAll(categories);
    productRepository.save(product);

    log.info("Updated categories for product ID {}: {}", productId, categoryIds);
  }

  @Transactional
  public void updateImages(Long productId, List<Long> imageIds) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found " + productId));

    List<Image> images = imageService.getImagesByIds(imageIds);

    product.getImages().clear();
    product.getImages().addAll(images);

    productRepository.save(product);
    log.info("Updated images for product ID {}: {}", productId, imageIds);
  }

  @Transactional(readOnly = true)
  public List<Long> getImagesIds(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found " + productId));

    return product.getImages().stream().map(Image::getId).toList();
  }
}
