package ru.nskopt.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.nskopt.entities.Product;
import ru.nskopt.entities.requests.UpdateProductRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductMapper implements Mapper<Product, UpdateProductRequest> {

  @Override
  public Product map(UpdateProductRequest value) {
    Product product = new Product();
    updateProductFields(product, value);
    return product;
  }

  @Override
  public void update(Product dest, UpdateProductRequest src) {
    updateProductFields(dest, src);
  }

  private void updateProductFields(Product product, UpdateProductRequest updateProductRequest) {
    product.setAvailability(updateProductRequest.getAvailability());

    product.setCost(updateProductRequest.getCost());

    product.setDescription(updateProductRequest.getDescription());
    product.setName(updateProductRequest.getName());
  }
}
