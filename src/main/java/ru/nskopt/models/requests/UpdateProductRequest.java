package ru.nskopt.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.nskopt.models.entities.Cost;

@Data
public class UpdateProductRequest {
  @NotBlank(message = "Name cannot be blank")
  @Size(min = 3, max = 16, message = "The length of the name should be in the range from 3 to 16")
  @Schema(description = "Название товара", example = "Кроссовки Nike air Monarch IV")
  private String name;

  @Valid
  @Schema(description = "Стоимость товара")
  private Cost cost;

  @PositiveOrZero
  @Schema(description = "Количество товара в наличии", example = "42")
  private int availability;

  @Schema(description = "Описание товара", example = "Материал ткань")
  private String description;
}
