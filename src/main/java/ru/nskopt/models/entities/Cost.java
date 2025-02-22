package ru.nskopt.models.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Cost {
  @PositiveOrZero(message = "Wholesale cost must be positive or zero")
  @Schema(description = "Оптовая стоимость в рублях", example = "1347")
  private BigDecimal wholesalePrice;

  @Positive(message = "Retail cost must be positive")
  @Schema(description = "Розничная стоимость в рублях", example = "4296")
  private BigDecimal retailPrice;
}
