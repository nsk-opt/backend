package ru.nskopt.dto.product;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUserResponse {
  private Long id;
  private String name;
  private String description;

  private BigDecimal price;
  private Long availability;
}
