package ru.nskopt.mappers;

import java.util.Set;
import org.mapstruct.*;
import ru.nskopt.dto.product.ProductUpdateRequest;
import ru.nskopt.dto.product.ProductUserResponse;
import ru.nskopt.entities.Product;
import ru.nskopt.entities.image.Image;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
  Product toProduct(ProductUpdateRequest request);

  @Mapping(target = "price", source = "cost.retailPrice")
  @Mapping(target = "imagesIds", source = "images")
  ProductUserResponse toUserResponse(Product product);

  void updateProductFromRequest(ProductUpdateRequest request, @MappingTarget Product product);

  default Long[] mapImages(Set<Image> images) {
    return images.stream().map(Image::getId).toArray(Long[]::new);
  }
}
