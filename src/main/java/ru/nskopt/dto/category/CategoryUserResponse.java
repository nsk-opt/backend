package ru.nskopt.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUserResponse {
  private Long id;
  private String name;
  private Long[] imagesIds;
}
