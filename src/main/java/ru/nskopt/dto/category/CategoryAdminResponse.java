package ru.nskopt.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAdminResponse {
  private Long id;
  private String name;
  private Long[] imagesIds;
}
