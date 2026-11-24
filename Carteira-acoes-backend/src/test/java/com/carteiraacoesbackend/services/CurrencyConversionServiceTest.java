package com.carteiraacoesbackend.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.carteiraacoesbackend.clients.PtaxClient;
import com.carteiraacoesbackend.dto.integrations.PtaxQuoteResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CurrencyConversionServiceTest {

  private final StubPtaxClient ptaxClient = new StubPtaxClient();
  private final CurrencyConversionService service = new CurrencyConversionService(ptaxClient);

  @Test
  void usesTheMostRecentValidPtaxSellingRateWithDecimalConversion() {
    ptaxClient.resposta =
        new PtaxQuoteResponse(
            Arrays.asList(
                new PtaxQuoteResponse.PtaxQuote("5.1000", "2026-09-03 13:00:00"),
                new PtaxQuoteResponse.PtaxQuote("5.2500", "2026-09-04 13:00:00")));

    TaxaUsdBrl taxa = service.consultarTaxaAtual().orElseThrow();

    assertThat(taxa.valor()).isEqualByComparingTo("5.2500");
    assertThat(taxa.dataHoraReferencia()).isEqualTo(OffsetDateTime.parse("2026-09-04T16:00:00Z"));
    assertThat(service.converter(new BigDecimal("12.34"), java.util.Optional.of(taxa)))
        .hasValueSatisfying(valor -> assertThat(valor).isEqualByComparingTo("64.785000"));
  }

  @Test
  void returnsNoRateForUnavailableOrInvalidPtaxResponses() {
    ptaxClient.falha = new RuntimeException("PTAX indisponível");
    assertThat(service.consultarTaxaAtual()).isEmpty();

    ptaxClient.falha = null;
    ptaxClient.resposta =
        new PtaxQuoteResponse(
            List.of(
                new PtaxQuoteResponse.PtaxQuote("0", "2026-09-04 13:00:00"),
                new PtaxQuoteResponse.PtaxQuote("invalida", "data invalida")));
    assertThat(service.consultarTaxaAtual()).isEmpty();
  }

  @Test
  void ignoresNullPtaxResponseValuesAndSupportsOffsetDateTimeInput() {
    ptaxClient.resposta = null;
    assertThat(service.consultarTaxaAtual()).isEmpty();

    ptaxClient.resposta = new PtaxQuoteResponse(null);
    assertThat(service.consultarTaxaAtual()).isEmpty();

    ptaxClient.resposta =
        new PtaxQuoteResponse(
            Arrays.asList(
                null, new PtaxQuoteResponse.PtaxQuote("5.10", "2026-09-04T13:00:00-03:00")));
    assertThat(service.consultarTaxaAtual())
        .hasValueSatisfying(taxa -> assertThat(taxa.valor()).isEqualByComparingTo("5.10"));
  }

  @Test
  void returnsEmptyWhenConversionInputIsInvalid() {
    TaxaUsdBrl taxa = new TaxaUsdBrl(new BigDecimal("5"), "BCB PTAX", OffsetDateTime.now());

    assertThat(service.converter(null, java.util.Optional.of(taxa))).isEmpty();
    assertThat(service.converter(new BigDecimal("-1"), java.util.Optional.of(taxa))).isEmpty();
    assertThat(service.converter(BigDecimal.ONE, java.util.Optional.empty())).isEmpty();
  }

  private static final class StubPtaxClient implements PtaxClient {
    private PtaxQuoteResponse resposta;
    private RuntimeException falha;

    @Override
    public PtaxQuoteResponse consultarCotacoes(
        String dataInicial, String dataFinal, int limite, String formato) {
      if (falha != null) {
        throw falha;
      }
      return resposta;
    }
  }
}
