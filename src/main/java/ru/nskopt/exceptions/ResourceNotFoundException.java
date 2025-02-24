package ru.nskopt.exceptions;

import java.util.List;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(Long id) {
    super("Resource with id " + id + " not found");
  }

  public ResourceNotFoundException(List<Long> ids) {
    super("Resources with ids " + ids + " not found");
  }
}
