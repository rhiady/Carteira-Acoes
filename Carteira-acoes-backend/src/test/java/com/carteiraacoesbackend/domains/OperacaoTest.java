package com.carteiraacoesbackend.domains;

import static org.assertj.core.api.Assertions.assertThat;

import com.carteiraacoesbackend.domains.enums.TipoOperacao;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OperacaoTest {

  @Test
  void appliesZeroDefaultsAndCalculatesDerivedValues() {
    Operacao operacao = new Operacao();
    operacao.setQuantidade(new BigDecimal("10.0000"));
    operacao.setPrecoUnitario(new BigDecimal("31.8500"));

    operacao.onCreate();

    assertThat(operacao.getCorretagem()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(operacao.getImpostos()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(operacao.getValorAdicional()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(operacao.getValorBruto()).isEqualByComparingTo("318.50000000");
    assertThat(operacao.getValorLiquido()).isEqualByComparingTo("318.50000000");
    assertThat(operacao.getDataHora()).isNotNull();
    assertThat(operacao.getCreatedAt()).isNotNull();
  }

  @Test
  void calculatesNetValueWithCostsAndAdditionalValue() {
    Operacao operacao = new Operacao();
    operacao.setTipo(TipoOperacao.COMPRA);
    operacao.setQuantidade(new BigDecimal("2"));
    operacao.setPrecoUnitario(new BigDecimal("100"));
    operacao.setCorretagem(new BigDecimal("5"));
    operacao.setImpostos(new BigDecimal("10"));
    operacao.setValorAdicional(new BigDecimal("20"));

    operacao.recalcularValores();

    assertThat(operacao.getValorBruto()).isEqualByComparingTo("200");
    assertThat(operacao.getValorLiquido()).isEqualByComparingTo("235");
  }
}
