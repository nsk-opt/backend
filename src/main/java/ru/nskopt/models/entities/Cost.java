package ru.nskopt.models.entities;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Cost {
  @NotNull(message = "Wholesale price must exists")
  @PositiveOrZero(message = "Wholesale cost must be positive or zero")
  @Schema(description = "Оптовая стоимость в рублях", example = "1347")
  private BigDecimal wholesalePrice;

  @NotNull(message = "Retail price must exists")
  @Positive(message = "Retail cost must be positive")
  @Schema(description = "Розничная стоимость в рублях", example = "4296")
  private BigDecimal retailPrice;
}
