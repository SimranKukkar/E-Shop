package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException {
  private String fieldName;
  private String resourceName;
  private String field;
  private Long fieldId;

  public ResourceNotFoundException() {}

  public ResourceNotFoundException(String fieldName, String resourceName, String field) {
    super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
    this.fieldName = fieldName;
    this.resourceName = resourceName;
    this.field = field;
  }

  public ResourceNotFoundException(String field, Long fieldId, String resourceName) {
    super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
    this.field = field;
    this.fieldId = fieldId;
    this.resourceName = resourceName;
  }
}
