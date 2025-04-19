package ru.nskopt.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nskopt.entities.Cost;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponse {
  private Long id;
  private String name;
  private String description;
  private Long[] imagesIds;

  private Cost cost;
  private Long availability;
}
