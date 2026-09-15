package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.clients.PtaxClient;
import com.carteiraacoesbackend.dto.integrations.PtaxQuoteResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionService {
  private static final ZoneId PTAX_ZONE = ZoneId.of("America/Sao_Paulo");
  private static final DateTimeFormatter PTAX_DATE = DateTimeFormatter.ofPattern("MM-dd-yyyy");
  private static final int LOOKBACK_DAYS = 10;

  private final PtaxClient ptaxClient;

  public CurrencyConversionService(PtaxClient ptaxClient) {
    this.ptaxClient = ptaxClient;
  }

  public Optional<TaxaUsdBrl> consultarTaxaAtual() {
    LocalDate hoje = LocalDate.now(PTAX_ZONE);
    try {
      PtaxQuoteResponse resposta =
          ptaxClient.consultarCotacoes(
              hoje.minusDays(LOOKBACK_DAYS).format(PTAX_DATE), hoje.format(PTAX_DATE), 100, "json");
      if (resposta == null || resposta.value() == null) {
        return Optional.empty();
      }
      return resposta.value().stream()
          .map(this::paraTaxa)
          .flatMap(Optional::stream)
          .max(Comparator.comparing(TaxaUsdBrl::dataHoraReferencia));
    } catch (RuntimeException exception) {
      return Optional.empty();
    }
  }

  public Optional<BigDecimal> converter(BigDecimal valorEmUsd, Optional<TaxaUsdBrl> taxa) {
    if (valorEmUsd == null || valorEmUsd.signum() < 0 || taxa.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(valorEmUsd.multiply(taxa.get().valor()));
  }

  private Optional<TaxaUsdBrl> paraTaxa(PtaxQuoteResponse.PtaxQuote cotacao) {
    if (cotacao == null || cotacao.cotacaoVenda() == null || cotacao.dataHoraCotacao() == null) {
      return Optional.empty();
    }
    try {
      BigDecimal valor = new BigDecimal(cotacao.cotacaoVenda());
      if (valor.signum() <= 0) {
        return Optional.empty();
      }
      return Optional.of(
          new TaxaUsdBrl(valor, "BCB PTAX", parseDataHora(cotacao.dataHoraCotacao())));
    } catch (RuntimeException exception) {
      return Optional.empty();
    }
  }

  private OffsetDateTime parseDataHora(String dataHora) {
    try {
      return OffsetDateTime.parse(dataHora).withOffsetSameInstant(ZoneOffset.UTC);
    } catch (java.time.format.DateTimeParseException exception) {
      return LocalDateTime.parse(dataHora.replace(' ', 'T'))
          .atZone(PTAX_ZONE)
          .withZoneSameInstant(ZoneOffset.UTC)
          .toOffsetDateTime();
    }
  }
}
