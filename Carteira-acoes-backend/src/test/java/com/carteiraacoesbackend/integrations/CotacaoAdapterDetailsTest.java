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
  void forwardsNonBlankBrapiToken() {
    java.util.concurrent.atomic.AtomicReference<String> token =
        new java.util.concurrent.atomic.AtomicReference<>();
    BrapiClient client =
        (ticker, authorization) -> {
          token.set(authorization);
          return new BrapiQuoteResponse(
              List.of(
                  new BrapiQuoteResponse.Result(
                      new BigDecimal("10"), "Empresa", null, "2026-09-08T17:30:00Z")));
        };

    new BrapiCotacaoAdapter(client, "segredo").consultar("PETR4");

    assertEquals("segredo", token.get());
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
  void rejectsEmptyOrIncompleteBrapiResults() {
    for (BrapiQuoteResponse body :
        List.of(
            new BrapiQuoteResponse(null),
            new BrapiQuoteResponse(List.of()),
            new BrapiQuoteResponse(
                List.of(
                    new BrapiQuoteResponse.Result(
                        null, "Empresa", null, "2026-09-08T17:30:00Z"))))) {
      ApiException exception =
          assertThrows(
              ApiException.class,
              () -> new BrapiCotacaoAdapter((ticker, token) -> body, "").consultar("PETR4"));
      assertEquals("TICKER_NAO_ENCONTRADO", exception.getCode());
    }
  }

  @Test
  void rejectsNonPositivePriceMissingNameAndMalformedTimestamp() {
    BrapiQuoteResponse.Result base =
        new BrapiQuoteResponse.Result(new BigDecimal("10"), null, "Curto", "2026-09-08T17:30:00Z");
    for (BrapiQuoteResponse.Result result :
        List.of(
            new BrapiQuoteResponse.Result(BigDecimal.ZERO, "Empresa", null, "2026-09-08T17:30:00Z"),
            new BrapiQuoteResponse.Result(new BigDecimal("10"), "Empresa", null, " "),
            new BrapiQuoteResponse.Result(new BigDecimal("10"), null, null, "2026-09-08T17:30:00Z"),
            new BrapiQuoteResponse.Result(new BigDecimal("10"), null, "Curto", "invalido"))) {
      ApiException exception =
          assertThrows(
              ApiException.class,
              () ->
                  new BrapiCotacaoAdapter(
                          (ticker, token) -> new BrapiQuoteResponse(List.of(result)), "")
                      .consultar("PETR4"));
      assertEquals("EXTERNAL_API_INVALID_RESPONSE", exception.getCode());
    }
    assertEquals(
        "Curto",
        new BrapiCotacaoAdapter((ticker, token) -> new BrapiQuoteResponse(List.of(base)), "")
            .consultar("PETR4")
            .nomeEmpresa());
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
  void rejectsEveryInvalidTwelveDataShape() {
    for (TwelveDataQuoteResponse body :
        java.util.Arrays.asList(
            null,
            new TwelveDataQuoteResponse(null, "1", 1L),
            new TwelveDataQuoteResponse("IBM", "1", null),
            new TwelveDataQuoteResponse("IBM", "1", 0L),
            new TwelveDataQuoteResponse("IBM", "0", 1L),
            new TwelveDataQuoteResponse("IBM", "invalido", 1L))) {
      ApiException exception =
          assertThrows(
              ApiException.class,
              () ->
                  new TwelveDataCotacaoAdapter((ticker, authorization) -> body, "key")
                      .consultar("IBM"));
      assertEquals("EXTERNAL_API_INVALID_RESPONSE", exception.getCode());
    }
  }

  @Test
  void preservesNonNotFoundProviderErrors() {
    ApiException providerError =
        ApiException.external(
            org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
            "EXTERNAL_API_RATE_LIMIT",
            "limite");
    ApiException exception =
        assertThrows(
            ApiException.class,
            () ->
                new TwelveDataCotacaoAdapter(
                        (ticker, authorization) -> {
                          throw providerError;
                        },
                        "key")
                    .consultar("IBM"));
    assertEquals("EXTERNAL_API_RATE_LIMIT", exception.getCode());
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
