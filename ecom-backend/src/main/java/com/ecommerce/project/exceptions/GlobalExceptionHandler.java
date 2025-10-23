package com.ecommerce.project.exceptions;

import com.ecommerce.project.payload.APIResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    // System.out.println("errors: " + e.getBindingResult().getFieldErrors());
    e.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> {
              String fieldName = error.getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<APIResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
    String message = e.getMessage();
    Boolean status = false;
    return new ResponseEntity<>(new APIResponse(message, status), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(APIException.class)
  public ResponseEntity<APIResponse> handleAPIException(APIException e) {
    String message = e.getMessage();
    Boolean status = false;
    return new ResponseEntity<>(new APIResponse(message, status), HttpStatus.BAD_REQUEST);
  }
}
