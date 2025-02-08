package ru.nskopt.exceptions;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(Long id) {
    super("Resource with id " + id + " not found");
  }
}
