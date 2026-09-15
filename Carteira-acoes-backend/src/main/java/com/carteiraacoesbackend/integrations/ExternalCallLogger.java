package com.carteiraacoesbackend.integrations;

import com.carteiraacoesbackend.exceptions.ApiException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExternalCallLogger {

  private static final Logger LOG = LoggerFactory.getLogger(ExternalCallLogger.class);

  public <T> T execute(String provider, String operation, String identifier, Supplier<T> call) {
    long startedAt = System.nanoTime();
    try {
      T response = call.get();
      log(provider, operation, identifier, "SUCCESS", elapsedMillis(startedAt), null);
      return response;
    } catch (RuntimeException exception) {
      String error =
          exception instanceof ApiException apiException
              ? apiException.getCode()
              : exception.getClass().getSimpleName();
      log(provider, operation, identifier, "FAILURE", elapsedMillis(startedAt), error);
      throw exception;
    }
  }

  private long elapsedMillis(long startedAt) {
    return (System.nanoTime() - startedAt) / 1_000_000;
  }

  private void log(
      String provider,
      String operation,
      String identifier,
      String status,
      long durationMs,
      String error) {
    LOG.info(
        "external_call provider={} operation={} identifier={} status={} durationMs={} error={}",
        provider,
        operation,
        identifier,
        status,
        durationMs,
        error);
  }
}
