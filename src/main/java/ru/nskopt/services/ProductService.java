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
import ru.nskopt.models.requests.UpdateProductRequest;
import ru.nskopt.repositories.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final Mapper<Product, UpdateProductRequest> productMapper;
  private final CategoryService categoryService;

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public Product findById(Long id) {
    return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
  }

  public Product save(UpdateProductRequest updateProductRequest) {
    log.info("Save {}", updateProductRequest);

    return productRepository.save(productMapper.map(updateProductRequest));
  }

  public Product update(Long id, UpdateProductRequest updateProductRequest) {
    return productRepository.findById(id).map(existingProduct -> {
      productMapper.update(existingProduct, updateProductRequest);

      log.info("Update {}", existingProduct);

      return productRepository.save(existingProduct);
    }).orElseThrow(() -> new ResourceNotFoundException(id));
  }

  public void deleteById(Long id) {
    if (!productRepository.existsById(id))
      throw new ResourceNotFoundException(id);

    log.info("Delete product with id {}", id);

    productRepository.deleteById(id);
  }

  public Set<Category> getCategoriesByProductId(Long id) {
    if (!productRepository.existsById(id))
      throw new ResourceNotFoundException(id);

    return productRepository.findCategoriesByProductId(id);
  }

  @Transactional
  public void addCategory(Long productId, Long categoryId) {
    Product product = findById(productId);
    Category category = categoryService.findById(categoryId);

    product.getCategories().add(category);
    productRepository.save(product);

    log.info("Add {} to {}", category, product);
  }

  public void removeCategory(Long productId, Long categoryId) {
    Product product = findById(productId);

    product.getCategories().removeIf(category -> category.getId().equals(categoryId));
    productRepository.save(product);

    log.info("Remove category with id {} from {}", categoryId, productId);
  }
}
