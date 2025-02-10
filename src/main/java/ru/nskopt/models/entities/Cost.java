package ru.nskopt.models.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Cost {
  @PositiveOrZero
  @Schema(description = "Wholesale cost in rubles", example = "1347")
  private BigDecimal wholesalePrice;

  @Positive
  @Schema(description = "Retail cost in rubles", example = "4296")
  private BigDecimal retailPrice;
}
