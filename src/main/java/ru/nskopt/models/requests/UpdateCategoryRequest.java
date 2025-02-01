package ru.nskopt.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.nskopt.models.dtos.ImageDto;

@Data
public class UpdateCategoryRequest {
  @NotBlank(message = "Name cannot be blank")
  @Size(min = 3, max = 16, message = "The length of the name should be in the range from 3 to 16")
  private String name;

  private ImageDto image;
}
