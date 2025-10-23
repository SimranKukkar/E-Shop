package com.ecommerce.project.exceptions;

public class APIException extends RuntimeException {
  private Long serialNumber = 1L;

  public APIException(String message) {
    super(message);
  }
}
