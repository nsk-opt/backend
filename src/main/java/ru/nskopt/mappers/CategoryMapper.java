package ru.nskopt.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nskopt.models.dtos.ImageDto;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Image;
import ru.nskopt.models.requests.UpdateCategoryRequest;

@Component
@RequiredArgsConstructor
public class CategoryMapper implements Mapper<Category, UpdateCategoryRequest> {

  private final Mapper<Image, ImageDto> imageMapper;

  @Override
  public Category map(UpdateCategoryRequest value) {
    Category category = new Category();
    updateCategoryFields(category, value);
    return category;
  }

  @Override
  public void update(Category dest, UpdateCategoryRequest src) {
    updateCategoryFields(dest, src);
  }

  private void updateCategoryFields(Category category, UpdateCategoryRequest categoryRequest) {
    category.setImage(imageMapper.map(categoryRequest.getImage()));
    category.setName(categoryRequest.getName());
  }
}
