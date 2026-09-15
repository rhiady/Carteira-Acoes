package com.carteiraacoesbackend.facades;

import com.carteiraacoesbackend.clients.BrasilApiClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.ExternalCallLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CvmFacade {
  private final BrasilApiClient client;
  private final ExternalCallLogger externalCallLogger;

  public CvmFacade(BrasilApiClient client) {
    this(client, new ExternalCallLogger());
  }

  @Autowired
  public CvmFacade(BrasilApiClient client, ExternalCallLogger externalCallLogger) {
    this.client = client;
    this.externalCallLogger = externalCallLogger;
  }

  public BrasilApiCvmBrokerResponse consultarCorretora(String cnpj) {
    String normalizado = limpar(cnpj);
    if (normalizado.length() != 14)
      throw ApiException.unprocessable("CNPJ_INVALIDO", "CNPJ deve conter 14 dígitos.");
    try {
      BrasilApiCvmBrokerResponse corretora =
          externalCallLogger.execute(
              "brasil-api",
              "cvm-broker",
              normalizado,
              () -> client.consultarCorretoraCvm(normalizado));
      return validarRegistro(corretora, normalizado);
    } catch (ApiException exception) {
      if ("EXTERNAL_RESOURCE_NOT_FOUND".equals(exception.getCode())) throw naoRegistrada();
      throw exception;
    } catch (Exception exception) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
          "EXTERNAL_API_UNAVAILABLE",
          "Não foi possível consultar o cadastro da CVM.");
    }
  }

  private BrasilApiCvmBrokerResponse validarRegistro(
      BrasilApiCvmBrokerResponse corretora, String cnpj) {
    if (corretora == null || !cnpj.equals(limpar(corretora.cnpj())) || emBranco(corretora.status()))
      throw ApiException.external(
          org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
          "EXTERNAL_API_UNAVAILABLE",
          "Não foi possível validar o cadastro da CVM.");
    if (!registroAtivo(corretora.status())) throw naoRegistrada();
    return corretora;
  }

  private boolean registroAtivo(String status) {
    String normalizado = status.trim().toUpperCase(java.util.Locale.ROOT);
    return "ATIVA".equals(normalizado) || "EM FUNCIONAMENTO NORMAL".equals(normalizado);
  }

  private boolean emBranco(String valor) {
    return valor == null || valor.isBlank();
  }

  private ApiException naoRegistrada() {
    return ApiException.unprocessable(
        "CORRETORA_NAO_REGISTRADA_CVM", "A corretora não possui registro ativo na CVM.");
  }

  private String limpar(Object valor) {
    return valor == null ? "" : valor.toString().replaceAll("\\D", "");
  }
}
