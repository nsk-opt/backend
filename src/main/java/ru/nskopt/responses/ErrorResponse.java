package ru.nskopt.responses;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
  private LocalDateTime timestamp = LocalDateTime.now();

  private final String error;
}
