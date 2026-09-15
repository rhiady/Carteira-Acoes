package com.carteiraacoesbackend.clients;

import com.carteiraacoesbackend.exceptions.ApiException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class FeignErrorDecoder implements ErrorDecoder {
  @Override
  public Exception decode(String methodKey, Response response) {
    return switch (response.status()) {
      case 404 ->
          ApiException.notFound(
              "EXTERNAL_RESOURCE_NOT_FOUND", "Recurso não encontrado no provedor externo.");
      case 401, 403 ->
          ApiException.external(
              HttpStatus.BAD_GATEWAY,
              "EXTERNAL_API_AUTHENTICATION",
              "Falha de autenticação no provedor externo.");
      case 429 ->
          ApiException.external(
              HttpStatus.TOO_MANY_REQUESTS,
              "EXTERNAL_API_RATE_LIMIT",
              "Limite de uso do provedor externo excedido.");
      case 502, 503, 504 ->
          new RetryableException(
              response.status(),
              "Provedor externo indisponível.",
              response.request().httpMethod(),
              null,
              (java.util.Date) null,
              response.request());
      case 500 ->
          ApiException.external(
              HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", "Falha no provedor externo.");
      default ->
          ApiException.external(
              HttpStatus.BAD_GATEWAY,
              "EXTERNAL_API_ERROR",
              "Falha na comunicação com o provedor externo.");
    };
  }
}
