package ru.nskopt.responses;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ErrorResponse {
  private LocalDateTime timestamp = LocalDateTime.now();

  private final String error;
}
