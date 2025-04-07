package ru.nskopt.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nskopt.entities.Category;
import ru.nskopt.entities.requests.UpdateCategoryRequest;

@Component
@RequiredArgsConstructor
public class CategoryMapper implements Mapper<Category, UpdateCategoryRequest> {

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
    category.setName(categoryRequest.getName());
  }
}
