package ru.nskopt.mappers;

import org.mapstruct.*;
import ru.nskopt.dto.category.CategoryUpdateRequest;
import ru.nskopt.dto.category.CategoryUserResponse;
import ru.nskopt.entities.Category;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

  Category toCategory(CategoryUpdateRequest request);

  CategoryUserResponse toUserResponse(Category category);

  void updateCategoryFromRequest(CategoryUpdateRequest request, @MappingTarget Category category);
}
