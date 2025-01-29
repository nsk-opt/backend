package ru.nskopt.services;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import ru.nskopt.models.Category;
import ru.nskopt.models.Product;
import ru.nskopt.repositories.ProductRepository;

@Log
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public Optional<Product> findById(Long id) {
    return productRepository.findById(id);
  }

  public Product save(Product product) {
    log.info("Save product " + product);
    return productRepository.save(product);
  }

  public void deleteById(Long id) {
    productRepository.deleteById(id);
  }

  public void addCategoryToProduct(Long productId, Category category) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

    product.getCategories().add(category);
    productRepository.save(product);

    log.info("Add category \"" + category.getName() + "\" to " + product.getName());
  }

  public void removeCategoryFromProduct(Long productId, Long categoryId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

    product.getCategories().removeIf(category -> category.getId().equals(categoryId));
    productRepository.save(product);

    log.info("Remove category with id \"" + categoryId + "\" from " + product.getName());
  }
}
