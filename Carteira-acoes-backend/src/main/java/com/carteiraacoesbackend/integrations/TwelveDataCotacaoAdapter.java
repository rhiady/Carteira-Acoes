package com.carteiraacoesbackend.integrations;

import com.carteiraacoesbackend.clients.TwelveDataClient;
import com.carteiraacoesbackend.dto.integrations.TwelveDataQuoteResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwelveDataCotacaoAdapter {

  private final TwelveDataClient client;
  private final String apiKey;
  private final ExternalCallLogger externalCallLogger;

  public TwelveDataCotacaoAdapter(TwelveDataClient client, String apiKey) {
    this(client, apiKey, new ExternalCallLogger());
  }

  @Autowired
  public TwelveDataCotacaoAdapter(
      TwelveDataClient client,
      @Value("${TWELVE_DATA_API_KEY:}") String apiKey,
      ExternalCallLogger externalCallLogger) {
    this.client = client;
    this.apiKey = apiKey;
    this.externalCallLogger = externalCallLogger;
  }

  public Cotacao consultar(String ticker) {
    try {
      TwelveDataQuoteResponse body =
          externalCallLogger.execute(
              "twelve-data", "quote", ticker, () -> client.consultar(ticker, "apikey " + apiKey));
      return normalizar(body);
    } catch (ApiException exception) {
      if ("EXTERNAL_RESOURCE_NOT_FOUND".equals(exception.getCode())) {
        throw ApiException.notFound(
            "TICKER_NAO_ENCONTRADO", "Ticker não encontrado no provedor de mercado.");
      }
      throw exception;
    }
  }

  private Cotacao normalizar(TwelveDataQuoteResponse body) {
    if (body == null
        || body.name() == null
        || body.name().isBlank()
        || body.close() == null
        || body.timestamp() == null
        || body.timestamp() <= 0) {
      throw respostaInvalida();
    }
    try {
      BigDecimal preco = new BigDecimal(body.close());
      if (preco.signum() <= 0) {
        throw respostaInvalida();
      }
      OffsetDateTime dataHora =
          OffsetDateTime.ofInstant(Instant.ofEpochSecond(body.timestamp()), ZoneOffset.UTC);
      return new Cotacao(preco, dataHora, body.name());
    } catch (NumberFormatException | java.time.DateTimeException exception) {
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
