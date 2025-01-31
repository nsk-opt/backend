package ru.nskopt.models.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Embeddable
public class Cost {
  @PositiveOrZero private BigDecimal wholesalePrice;

  @Positive private BigDecimal retailPrice;
}
