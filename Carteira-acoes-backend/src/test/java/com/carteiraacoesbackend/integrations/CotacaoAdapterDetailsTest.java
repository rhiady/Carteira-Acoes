package com.carteiraacoesbackend.integrations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.carteiraacoesbackend.clients.BrapiClient;
import com.carteiraacoesbackend.clients.TwelveDataClient;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.dto.integrations.BrapiQuoteResponse;
import com.carteiraacoesbackend.dto.integrations.TwelveDataQuoteResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class CotacaoAdapterDetailsTest {

  @Test
  void importsBrazilianCompanyNameFromBrapiQuote() {
    BrapiClient client =
        (ticker, token) ->
            new BrapiQuoteResponse(
                List.of(
                    new BrapiQuoteResponse.Result(
                        new BigDecimal("38.50"),
                        "Petróleo Brasileiro S.A.",
                        "PETROBRAS PN",
                        "2026-09-08T17:30:00-03:00")));

    Cotacao quote = new BrapiCotacaoAdapter(client, "").consultar("PETR4");

    assertEquals("Petróleo Brasileiro S.A.", quote.nomeEmpresa());
    assertEquals(OffsetDateTime.parse("2026-09-08T20:30:00Z"), quote.dataHora());
  }

  @Test
  void mapsEmptyBrapiBodyToTickerNotFound() {
    BrapiClient client = (ticker, token) -> null;

    ApiException exception =
        assertThrows(
            ApiException.class, () -> new BrapiCotacaoAdapter(client, "").consultar("INVALID"));

    assertEquals("TICKER_NAO_ENCONTRADO", exception.getCode());
  }

  @Test
  void rejectsBrapiQuoteWithoutAnAuthoritativeTimestamp() {
    BrapiClient client =
        (ticker, token) ->
            new BrapiQuoteResponse(
                List.of(
                    new BrapiQuoteResponse.Result(
                        new BigDecimal("38.50"), "Petrobras", null, null)));

    ApiException exception =
        assertThrows(
            ApiException.class, () -> new BrapiCotacaoAdapter(client, "").consultar("PETR4"));

    assertEquals("EXTERNAL_API_INVALID_RESPONSE", exception.getCode());
    assertEquals(502, exception.getStatus().value());
  }

  @Test
  void importsAmericanQuoteDetailsFromTwelveData() {
    AtomicReference<String> authorization = new AtomicReference<>();
    TwelveDataClient client =
        (ticker, header) -> {
          authorization.set(header);
          return new TwelveDataQuoteResponse(
              "International Business Machines Corporation", "123.45", 1_763_000_000L);
        };

    Cotacao quote = new TwelveDataCotacaoAdapter(client, "key").consultar("IBM");

    assertEquals("International Business Machines Corporation", quote.nomeEmpresa());
    assertEquals(new BigDecimal("123.45"), quote.preco());
    assertEquals(OffsetDateTime.parse("2025-11-13T02:13:20Z"), quote.dataHora());
    assertEquals("apikey key", authorization.get());
  }

  @Test
  void rejectsIncompleteTwelveDataResponse() {
    TwelveDataClient client =
        (ticker, authorization) -> new TwelveDataQuoteResponse("IBM", null, 1_763_000_000L);

    ApiException exception =
        assertThrows(
            ApiException.class, () -> new TwelveDataCotacaoAdapter(client, "key").consultar("IBM"));

    assertEquals("EXTERNAL_API_INVALID_RESPONSE", exception.getCode());
  }

  @Test
  void mapsMissingTwelveDataSymbolToTickerNotFound() {
    TwelveDataClient client =
        (ticker, authorization) -> {
          throw ApiException.notFound("EXTERNAL_RESOURCE_NOT_FOUND", "não encontrado");
        };

    ApiException exception =
        assertThrows(
            ApiException.class,
            () -> new TwelveDataCotacaoAdapter(client, "key").consultar("INVALID"));

    assertEquals("TICKER_NAO_ENCONTRADO", exception.getCode());
  }

  @Test
  void selectsTheProviderForEachMarket() {
    AtomicBoolean brapiCalled = new AtomicBoolean();
    AtomicBoolean twelveDataCalled = new AtomicBoolean();
    BrapiClient brapiClient =
        (ticker, token) -> {
          brapiCalled.set(true);
          return new BrapiQuoteResponse(
              List.of(
                  new BrapiQuoteResponse.Result(
                      new BigDecimal("10.00"), "Brapi", null, "2026-09-08T17:30:00-03:00")));
        };
    TwelveDataClient twelveDataClient =
        (ticker, authorization) -> {
          twelveDataCalled.set(true);
          return new TwelveDataQuoteResponse("Twelve Data", "20.00", 1_763_000_000L);
        };
    DefaultCotacaoAdapter adapter =
        new DefaultCotacaoAdapter(
            new BrapiCotacaoAdapter(brapiClient, ""),
            new TwelveDataCotacaoAdapter(twelveDataClient, "key"));

    adapter.consultar("PETR4", Mercado.BRASIL);
    assertEquals(true, brapiCalled.get());
    assertEquals(false, twelveDataCalled.get());

    adapter.consultar("IBM", Mercado.EUA);
    assertEquals(true, twelveDataCalled.get());
  }
}
