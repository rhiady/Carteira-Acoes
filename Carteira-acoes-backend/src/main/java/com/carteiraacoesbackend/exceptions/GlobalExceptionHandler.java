package com.carteiraacoesbackend.exceptions;

import com.carteiraacoesbackend.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErroResponse> handleApi(
      ApiException exception, HttpServletRequest request) {
    return response(exception.getStatus(), exception.getCode(), exception.getMessage(), request);
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    ConstraintViolationException.class,
    HandlerMethodValidationException.class
  })
  public ResponseEntity<ErroResponse> handleValidation(
      Exception exception, HttpServletRequest request) {
    return response(
        HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Dados da requisição são inválidos.", request);
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class,
    MissingServletRequestParameterException.class
  })
  public ResponseEntity<ErroResponse> handleMalformedRequest(
      Exception exception, HttpServletRequest request) {
    return response(
        HttpStatus.BAD_REQUEST,
        "INVALID_REQUEST_FORMAT",
        "Formato da requisição é inválido.",
        request);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErroResponse> handleIntegrity(
      DataIntegrityViolationException exception, HttpServletRequest request) {
    return response(
        HttpStatus.CONFLICT,
        "DATA_INTEGRITY_VIOLATION",
        "Os dados informados violam uma regra de integridade.",
        request);
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<ErroResponse> handleOptimisticLock(
      OptimisticLockingFailureException exception, HttpServletRequest request) {
    return response(
        HttpStatus.CONFLICT,
        "CONCURRENT_MODIFICATION",
        "O recurso foi alterado por outra operação. Tente novamente.",
        request);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErroResponse> handleMethodNotAllowed(
      HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
    return response(
        HttpStatus.METHOD_NOT_ALLOWED,
        "METHOD_NOT_ALLOWED",
        "Método HTTP não permitido para este recurso.",
        request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErroResponse> handleUnexpected(
      Exception exception, HttpServletRequest request) {
    return response(
        HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocorreu um erro inesperado.", request);
  }

  private ResponseEntity<ErroResponse> response(
      HttpStatus status, String code, String message, HttpServletRequest request) {
    ErroResponse body =
        new ErroResponse(
            OffsetDateTime.now(ZoneOffset.UTC),
            status.value(),
            code,
            message,
            request.getRequestURI());
    return ResponseEntity.status(status).body(body);
  }
}
