package com.carteiraacoesbackend.integrations;

import com.carteiraacoesbackend.clients.BrapiClient;
import com.carteiraacoesbackend.dto.integrations.BrapiQuoteResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BrapiCotacaoAdapter {

  private final BrapiClient client;
  private final String token;
  private final ExternalCallLogger externalCallLogger;

  public BrapiCotacaoAdapter(BrapiClient client, String token) {
    this(client, token, new ExternalCallLogger());
  }

  @Autowired
  public BrapiCotacaoAdapter(
      BrapiClient client,
      @Value("${BRAPI_TOKEN:}") String token,
      ExternalCallLogger externalCallLogger) {
    this.client = client;
    this.token = token;
    this.externalCallLogger = externalCallLogger;
  }

  public Cotacao consultar(String ticker) {
    BrapiQuoteResponse body =
        externalCallLogger.execute(
            "brapi",
            "quote",
            ticker,
            () -> client.consultar(ticker, token.isBlank() ? null : token));
    if (body == null
        || body.results() == null
        || body.results().isEmpty()
        || body.results().getFirst().regularMarketPrice() == null) {
      throw ApiException.notFound(
          "TICKER_NAO_ENCONTRADO", "Ticker não encontrado no provedor de mercado.");
    }
    BrapiQuoteResponse.Result result = body.results().getFirst();
    if (result.regularMarketPrice().signum() <= 0
        || result.regularMarketTime() == null
        || result.regularMarketTime().isBlank()) {
      throw respostaInvalida();
    }
    String nomeEmpresa =
        result.longName() == null || result.longName().isBlank()
            ? result.shortName()
            : result.longName();
    if (nomeEmpresa == null || nomeEmpresa.isBlank()) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.BAD_GATEWAY,
          "EXTERNAL_API_INVALID_RESPONSE",
          "O provedor não retornou o nome da empresa.");
    }
    try {
      return new Cotacao(
          result.regularMarketPrice(),
          OffsetDateTime.parse(result.regularMarketTime())
              .withOffsetSameInstant(java.time.ZoneOffset.UTC),
          nomeEmpresa);
    } catch (java.time.format.DateTimeParseException exception) {
      throw respostaInvalida();
    }
  }

  private ApiException respostaInvalida() {
    return ApiException.external(
        org.springframework.http.HttpStatus.BAD_GATEWAY,
        "EXTERNAL_API_INVALID_RESPONSE",
        "O provedor retornou uma cotação inválida.");
  }
}
