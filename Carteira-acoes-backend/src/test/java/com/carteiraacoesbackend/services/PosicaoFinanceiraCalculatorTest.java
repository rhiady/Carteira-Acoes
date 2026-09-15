package com.carteiraacoesbackend.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.carteiraacoesbackend.domains.Operacao;
import com.carteiraacoesbackend.domains.enums.TipoOperacao;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PosicaoFinanceiraCalculatorTest {
  private final PosicaoFinanceiraCalculator calculator = new PosicaoFinanceiraCalculator();

  @Test
  void calculatesWeightedAverageSaleAndRealizedAndUnrealizedResults() {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "10", "30"),
                operacao(TipoOperacao.COMPRA, "10", "40"),
                operacao(TipoOperacao.VENDA, "5", "45")),
            new BigDecimal("45"));

    assertThat(resultado.precoMedioVenda()).isEqualByComparingTo("45");
    assertThat(resultado.precoMedio()).isEqualByComparingTo("35");
    assertThat(resultado.lucroPrejuizoRealizado()).isEqualByComparingTo("50");
    assertThat(resultado.lucroPrejuizoNaoRealizado()).isEqualByComparingTo("150");
    assertThat(resultado.lucroPrejuizo()).isEqualByComparingTo("200");
  }

  @Test
  void excludesSaleCostsFromAveragePriceAndDeductsThemFromRealizedResult() {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "10", "30"),
                operacao(TipoOperacao.VENDA, "5", "40", "2", "1", "0")),
            new BigDecimal("30"));

    assertThat(resultado.precoMedioVenda()).isEqualByComparingTo("40");
    assertThat(resultado.precoMedio()).isEqualByComparingTo("30");
    assertThat(resultado.lucroPrejuizoRealizado()).isEqualByComparingTo("47");
  }

  @Test
  void returnsNullAverageSalePriceWhenThereAreNoSales() {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(
            List.of(operacao(TipoOperacao.COMPRA, "10", "30")), new BigDecimal("30"));

    assertThat(resultado.precoMedioVenda()).isNull();
    assertThat(resultado.precoMedio()).isEqualByComparingTo("30");
    assertThat(resultado.lucroPrejuizo()).isEqualByComparingTo("0");
  }

  @Test
  void calculatesWeightedAverageSalePriceAcrossMultipleSales() {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "20", "10"),
                operacao(TipoOperacao.VENDA, "5", "30"),
                operacao(TipoOperacao.VENDA, "10", "36")),
            new BigDecimal("10"));

    assertThat(resultado.precoMedioVenda()).isEqualByComparingTo("34");
  }

  @Test
  void keepsQuoteDependentValuesNullWhenCurrentQuoteIsMissing() {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(List.of(operacao(TipoOperacao.COMPRA, "10", "30")), null);

    assertThat(resultado.precoMedio()).isEqualByComparingTo("30");
    assertThat(resultado.lucroPrejuizoRealizado()).isEqualByComparingTo("0");
    assertThat(resultado.precoMedioVenda()).isNull();
    assertThat(resultado.valorAtual()).isNull();
    assertThat(resultado.lucroPrejuizoNaoRealizado()).isNull();
    assertThat(resultado.lucroPrejuizo()).isNull();
  }

  @Test
  void calculatesWeightedAcquisitionCostAfterEachSuccessivePurchase() {
    Operacao primeira = operacao(TipoOperacao.COMPRA, "10", "30");
    Operacao segunda = operacao(TipoOperacao.COMPRA, "10", "40");
    Operacao terceira = operacao(TipoOperacao.COMPRA, "5", "50");

    assertThat(calculator.calcular(List.of(primeira), BigDecimal.ZERO).precoMedio())
        .isEqualByComparingTo("30");
    assertThat(calculator.calcular(List.of(primeira, segunda), BigDecimal.ZERO).precoMedio())
        .isEqualByComparingTo("35");
    assertThat(
            calculator.calcular(List.of(primeira, segunda, terceira), BigDecimal.ZERO).precoMedio())
        .isEqualByComparingTo("38");
  }

  @Test
  void preservesAverageCostOnPartialSaleAndKeepsRealizedResultAfterTotalSale() {
    PosicaoFinanceiraCalculator.Resultado parcial =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "20", "35"), operacao(TipoOperacao.VENDA, "5", "45")),
            new BigDecimal("45"));
    assertThat(parcial.precoMedio()).isEqualByComparingTo("35");
    assertThat(parcial.lucroPrejuizoRealizado()).isEqualByComparingTo("50");

    PosicaoFinanceiraCalculator.Resultado total =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "10", "30"),
                operacao(TipoOperacao.VENDA, "10", "40")),
            new BigDecimal("50"));
    assertThat(total.valorAtual()).isEqualByComparingTo("0");
    assertThat(total.precoMedio()).isEqualByComparingTo("0");
    assertThat(total.lucroPrejuizoRealizado()).isEqualByComparingTo("100");
    assertThat(total.lucroPrejuizoNaoRealizado()).isEqualByComparingTo("0");
    assertThat(total.lucroPrejuizo()).isEqualByComparingTo("100");
  }

  @Test
  void accumulatesProfitAndLossAcrossMultipleSales() {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "10", "30"),
                operacao(TipoOperacao.COMPRA, "10", "40"),
                operacao(TipoOperacao.VENDA, "5", "45"),
                operacao(TipoOperacao.VENDA, "5", "30")),
            new BigDecimal("35"));

    assertThat(resultado.lucroPrejuizoRealizado()).isEqualByComparingTo("25");
  }

  @Test
  void calculatesCurrentAndTotalResultsAboveBelowAndAtCost() {
    List<Operacao> historico =
        List.of(operacao(TipoOperacao.COMPRA, "20", "35"), operacao(TipoOperacao.VENDA, "5", "45"));

    PosicaoFinanceiraCalculator.Resultado acima =
        calculator.calcular(historico, new BigDecimal("45"));
    assertThat(acima.valorAtual()).isEqualByComparingTo("675");
    assertThat(acima.lucroPrejuizoNaoRealizado()).isEqualByComparingTo("150");
    assertThat(acima.lucroPrejuizo()).isEqualByComparingTo("200");

    PosicaoFinanceiraCalculator.Resultado abaixo =
        calculator.calcular(historico, new BigDecimal("30"));
    assertThat(abaixo.lucroPrejuizoNaoRealizado()).isEqualByComparingTo("-75");
    assertThat(abaixo.lucroPrejuizo()).isEqualByComparingTo("-25");

    PosicaoFinanceiraCalculator.Resultado igual =
        calculator.calcular(historico, new BigDecimal("35"));
    assertThat(igual.lucroPrejuizoNaoRealizado()).isEqualByComparingTo("0");
    assertThat(igual.lucroPrejuizo()).isEqualByComparingTo("50");
  }

  @Test
  void preservesRealizedHistoryAfterPartialSaleAndPositionReopening() {
    PosicaoFinanceiraCalculator.Resultado aposCompraParcial =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "20", "35"),
                operacao(TipoOperacao.VENDA, "5", "45"),
                operacao(TipoOperacao.COMPRA, "5", "50")),
            new BigDecimal("50"));
    assertThat(aposCompraParcial.precoMedio()).isEqualByComparingTo("38.75");
    assertThat(aposCompraParcial.lucroPrejuizoRealizado()).isEqualByComparingTo("50");

    PosicaoFinanceiraCalculator.Resultado reaberta =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "10", "30"),
                operacao(TipoOperacao.VENDA, "10", "40"),
                operacao(TipoOperacao.COMPRA, "5", "50")),
            new BigDecimal("50"));
    assertThat(reaberta.precoMedio()).isEqualByComparingTo("50");
    assertThat(reaberta.lucroPrejuizoRealizado()).isEqualByComparingTo("100");
    assertThat(reaberta.lucroPrejuizo()).isEqualByComparingTo("100");
  }

  @Test
  void includesPurchaseAndSaleCostsInTheirDocumentedFinancialEffects() {
    PosicaoFinanceiraCalculator.Resultado resultado =
        calculator.calcular(
            List.of(
                operacao(TipoOperacao.COMPRA, "10", "30", "4", "3", "3"),
                operacao(TipoOperacao.VENDA, "5", "40", "1", "1", "1")),
            new BigDecimal("31"));

    assertThat(resultado.precoMedio()).isEqualByComparingTo("31");
    assertThat(resultado.precoMedioVenda()).isEqualByComparingTo("40");
    assertThat(resultado.lucroPrejuizoRealizado()).isEqualByComparingTo("42");
    assertThat(resultado.lucroPrejuizoNaoRealizado()).isEqualByComparingTo("0");
  }

  private Operacao operacao(TipoOperacao tipo, String quantidade, String preco) {
    return operacao(tipo, quantidade, preco, "0", "0", "0");
  }

  private Operacao operacao(
      TipoOperacao tipo,
      String quantidade,
      String preco,
      String corretagem,
      String impostos,
      String adicional) {
    Operacao operacao = new Operacao();
    operacao.setTipo(tipo);
    operacao.setQuantidade(new BigDecimal(quantidade));
    operacao.setPrecoUnitario(new BigDecimal(preco));
    operacao.setCorretagem(new BigDecimal(corretagem));
    operacao.setImpostos(new BigDecimal(impostos));
    operacao.setValorAdicional(new BigDecimal(adicional));
    operacao.recalcularValores();
    return operacao;
  }
}
