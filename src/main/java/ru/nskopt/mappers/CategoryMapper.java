package ru.nskopt.mappers;

import java.util.Set;
import org.mapstruct.*;
import ru.nskopt.dto.category.CategoryUpdateRequest;
import ru.nskopt.dto.category.CategoryUserResponse;
import ru.nskopt.entities.Category;
import ru.nskopt.entities.image.Image;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

  Category toCategory(CategoryUpdateRequest request);

  @Mapping(target = "imagesIds", source = "images")
  CategoryUserResponse toUserResponse(Category category);

  void updateCategoryFromRequest(CategoryUpdateRequest request, @MappingTarget Category category);

  default Long[] mapImages(Set<Image> images) {
    return images.stream().map(Image::getId).toArray(Long[]::new);
  }
}
