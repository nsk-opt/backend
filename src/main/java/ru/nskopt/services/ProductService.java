package ru.nskopt.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Product;
import ru.nskopt.repositories.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryService categoryService;

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public Product findById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
  }

  public Product save(Product product) {
    log.info("Save {}", product);
    return productRepository.save(product);
  }

  public void deleteById(Long id) {
    if (!productRepository.existsById(id))
      throw new ResourceNotFoundException("Product with id " + id + " not found");

    log.info("Delete product with id {}", id);

    productRepository.deleteById(id);
  }

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
