package ru.nskopt.advices;

import io.jsonwebtoken.security.SignatureException;
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
import ru.nskopt.exceptions.AuthenticationFailedException;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.exceptions.UnsupportedImageFormatException;
import ru.nskopt.exceptions.UserExistsException;
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

  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  @ExceptionHandler(UnsupportedImageFormatException.class)
  @Operation(
      summary = "Обработка ошибки 'Неподдерживаемый формат изображения'",
      description =
          "Возвращает сообщение об ошибке, если загружаемый формат изображения не поддерживается.")
  public ErrorResponse handleUnsupportedImageFormatException(
      UnsupportedImageFormatException e, WebRequest request) {
    log.info("Unsupported image format: {}, Request details: {}", e.getMessage(), request);
    return new ErrorResponse("Unsupported image format: " + e.getMessage());
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(UserExistsException.class)
  @Operation(
      summary = "Обработка ошибки 'Пользователь уже существует'",
      description =
          "Возвращает сообщение об ошибке, если пользователь с таким email уже существует.")
  public ErrorResponse handleUserExistsException(UserExistsException e, WebRequest request) {
    log.info("User already exists: {}, Request details: {}", e.getMessage(), request);
    return new ErrorResponse("User with email " + e.getMessage() + " already exists");
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AuthenticationFailedException.class)
  public ErrorResponse handleAuthenticationFailedException(
      AuthenticationFailedException e, WebRequest request) {
    log.info("Authentication failed: {}, Request details: {}", e.getMessage(), request);
    return new ErrorResponse("Authentication failed: " + e.getMessage());
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(SignatureException.class)
  public ErrorResponse handleSignatureException(SignatureException e, WebRequest request) {
    log.info("Jwt is invalids: {}, Request details: {}", e.getMessage(), request);
    return new ErrorResponse("JWT is invalid: " + e.getMessage());
  }
}
