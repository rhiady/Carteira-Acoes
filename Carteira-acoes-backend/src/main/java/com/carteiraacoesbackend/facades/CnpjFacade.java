package com.carteiraacoesbackend.facades;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.ExternalCallLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CnpjFacade {
  private final BrasilApiClient client;
  private final ExternalCallLogger externalCallLogger;

  public CnpjFacade(BrasilApiClient client) {
    this(client, new ExternalCallLogger());
  }

  @Autowired
  public CnpjFacade(BrasilApiClient client, ExternalCallLogger externalCallLogger) {
    this.client = client;
    this.externalCallLogger = externalCallLogger;
  }

  public BrasilApiCnpjResponse consultar(String cnpj) {
    String normalizado = somenteDigitos(cnpj);
    if (normalizado.length() != 14)
      throw ApiException.unprocessable("CNPJ_INVALIDO", "CNPJ deve conter 14 dígitos.");
    try {
      BrasilApiCnpjResponse response =
          externalCallLogger.execute(
              "brasil-api", "cnpj", normalizado, () -> client.consultarCnpj(normalizado));
      if (response == null) {
        throw ApiException.external(
            org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
            "EXTERNAL_API_UNAVAILABLE",
            "Não foi possível consultar o CNPJ.");
      }
      return response;
    } catch (ApiException exception) {
      throw exception;
    } catch (Exception e) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
          "EXTERNAL_API_UNAVAILABLE",
          "Não foi possível consultar o CNPJ.");
    }
  }

  private String somenteDigitos(String valor) {
    return valor == null ? "" : valor.replaceAll("\\D", "");
  }
}
