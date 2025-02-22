package ru.nskopt.advices;

import io.swagger.v3.oas.annotations.Operation;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.responses.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ResourceNotFoundException.class)
  @Operation(
      summary = "Обработка ошибки 'Ресурс не найден'",
      description = "Возвращает сообщение об ошибке, если запрашиваемый ресурс не найден.")
  public ErrorResponse handleNotFoundException(ResourceNotFoundException e, WebRequest request) {
    log.info("Resource not found: {}, Request details: {}", e.getMessage(), request);
    return new ErrorResponse("message: " + e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoHandlerFoundException.class)
  @Operation(
      summary = "Обработка ошибки 'Обработчик не найден'",
      description = "Возвращает сообщение об ошибке, если обработчик для запроса не найден.")
  public ErrorResponse handleNoHandlerFoundException(
      NoHandlerFoundException e, WebRequest request) {
    return new ErrorResponse("Resource not found");
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @Operation(
      summary = "Обработка ошибок валидации",
      description = "Возвращает сообщение об ошибке, если данные запроса не прошли валидацию.")
  public ErrorResponse handleValidationExceptions(
      MethodArgumentNotValidException e, WebRequest request) {
    String validationErrors =
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));
    log.info(
        "Validation failed: {}, Errors: {}, Request details: {}", e, validationErrors, request);
    return new ErrorResponse("Validation error: " + validationErrors);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @Operation(
      summary = "Обработка ошибки 'Невозможно прочитать HTTP сообщение'",
      description = "Возвращает сообщение об ошибке, если запрос содержит невалидные данные.")
  public ErrorResponse handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e, WebRequest request) {
    log.info(
        "Failed to read HTTP message. Request details: {}, Message: {}", request, e.getMessage());
    return new ErrorResponse("Invalid input. Please check your request format or parameters.");
  }
}
