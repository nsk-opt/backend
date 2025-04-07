package ru.nskopt.mappers;

import org.mapstruct.*;
import ru.nskopt.dto.product.ProductUpdateRequest;
import ru.nskopt.dto.product.ProductUserResponse;
import ru.nskopt.entities.Product;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
  Product toProduct(ProductUpdateRequest request);

  @Mapping(target = "price", source = "cost.retailPrice")
  ProductUserResponse toUserResponse(Product product);

  void updateProductFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}
