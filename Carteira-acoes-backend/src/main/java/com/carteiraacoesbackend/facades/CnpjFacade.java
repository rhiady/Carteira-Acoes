package com.carteiraacoesbackend.facades;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.clients.CnpjWsClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCnpjResponse;
import com.carteiraacoesbackend.dto.integrations.CnpjWsResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.ExternalCallLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CnpjFacade {
  private final BrasilApiClient client;
  private final CnpjWsClient fallbackClient;
  private final ExternalCallLogger externalCallLogger;

  public CnpjFacade(BrasilApiClient client) {
    this(client, null, new ExternalCallLogger());
  }

  @Autowired
  public CnpjFacade(
      BrasilApiClient client, CnpjWsClient fallbackClient, ExternalCallLogger externalCallLogger) {
    this.client = client;
    this.fallbackClient = fallbackClient;
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
      if (fallbackClient != null && falhaExterna(exception)) return consultarFallback(normalizado);
      throw exception;
    } catch (Exception e) {
      if (fallbackClient != null) return consultarFallback(normalizado);
      throw ApiException.external(
          org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
          "EXTERNAL_API_UNAVAILABLE",
          "Não foi possível consultar o CNPJ.");
    }
  }

  private BrasilApiCnpjResponse consultarFallback(String cnpj) {
    try {
      CnpjWsResponse response =
          externalCallLogger.execute(
              "cnpj-ws", "cnpj", cnpj, () -> fallbackClient.consultarCnpj(cnpj));
      CnpjWsResponse.Estabelecimento estabelecimento =
          response == null ? null : response.estabelecimento();
      if (estabelecimento == null) throw indisponivel();
      return new BrasilApiCnpjResponse(
          response.razaoSocial(),
          estabelecimento.nomeFantasia(),
          estabelecimento.email(),
          estabelecimento.telefone(),
          estabelecimento.situacaoCadastral(),
          estabelecimento.cep(),
          estabelecimento.logradouro(),
          estabelecimento.numero(),
          estabelecimento.complemento(),
          estabelecimento.bairro(),
          estabelecimento.cidade() == null ? null : estabelecimento.cidade().nome(),
          estabelecimento.estado() == null ? null : estabelecimento.estado().sigla());
    } catch (ApiException exception) {
      throw exception;
    } catch (Exception exception) {
      throw indisponivel();
    }
  }

  private boolean falhaExterna(ApiException exception) {
    return exception.getStatus().is5xxServerError()
        || "EXTERNAL_API_ERROR".equals(exception.getCode());
  }

  private ApiException indisponivel() {
    return ApiException.external(
        org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
        "EXTERNAL_API_UNAVAILABLE",
        "Não foi possível consultar o CNPJ.");
  }

  private String somenteDigitos(String valor) {
    return valor == null ? "" : valor.replaceAll("\\D", "");
  }
}
