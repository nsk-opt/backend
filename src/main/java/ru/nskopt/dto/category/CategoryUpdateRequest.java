package ru.nskopt.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequest {
  @NotBlank(message = "Name cannot be blank")
  @Size(min = 3, max = 16, message = "The length of the name should be in the range from 3 to 16")
  @Schema(description = "Название категории", example = "Обувь")
  private String name;
}
