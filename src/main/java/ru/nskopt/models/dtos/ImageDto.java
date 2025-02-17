package ru.nskopt.models.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDto {
  @Pattern(
      regexp = "^https://.*$",
      message = "Link must be a valid URL starting with https")
  private String link;
}
