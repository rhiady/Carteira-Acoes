package com.carteiraacoesbackend.services;

import com.carteiraacoesbackend.domains.Operacao;
import com.carteiraacoesbackend.domains.enums.TipoOperacao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PosicaoFinanceiraCalculator {
  private static final BigDecimal ZERO = BigDecimal.ZERO;

  public Resultado calcular(List<Operacao> operacoes, BigDecimal cotacaoAtual) {
    BigDecimal quantidadeAtual = ZERO;
    BigDecimal custoRestante = ZERO;
    BigDecimal resultadoRealizado = ZERO;
    BigDecimal quantidadeVendida = ZERO;
    BigDecimal receitaBrutaVenda = ZERO;
    BigDecimal valorTotalComprado = ZERO;

    for (Operacao operacao : operacoes) {
      BigDecimal quantidade = operacao.getQuantidade();
      if (operacao.getTipo() == TipoOperacao.COMPRA) {
        quantidadeAtual = quantidadeAtual.add(quantidade);
        custoRestante = custoRestante.add(custoDaCompra(operacao));
        valorTotalComprado = valorTotalComprado.add(operacao.getValorBruto());
      } else {
        BigDecimal custoMedio = dividir(custoRestante, quantidadeAtual);
        BigDecimal custoBase = quantidade.multiply(custoMedio);
        quantidadeAtual = quantidadeAtual.subtract(quantidade);
        custoRestante = custoRestante.subtract(custoBase);
        resultadoRealizado =
            resultadoRealizado.add(receitaLiquidaVenda(operacao).subtract(custoBase));
        quantidadeVendida = quantidadeVendida.add(quantidade);
        receitaBrutaVenda = receitaBrutaVenda.add(operacao.getValorBruto());
      }
    }

    BigDecimal precoMedioVenda =
        quantidadeVendida.signum() == 0 ? null : dividir(receitaBrutaVenda, quantidadeVendida);
    BigDecimal precoMedio = dividir(custoRestante, quantidadeAtual);
    if (cotacaoAtual == null) {
      return new Resultado(
          null,
          precoMedio,
          precoMedioVenda,
          resultadoRealizado,
          null,
          null,
          custoRestante,
          valorTotalComprado,
          receitaBrutaVenda);
    }
    BigDecimal valorAtual = quantidadeAtual.multiply(cotacaoAtual);
    BigDecimal naoRealizado = valorAtual.subtract(custoRestante);
    return new Resultado(
        valorAtual,
        precoMedio,
        precoMedioVenda,
        resultadoRealizado,
        naoRealizado,
        resultadoRealizado.add(naoRealizado),
        custoRestante,
        valorTotalComprado,
        receitaBrutaVenda);
  }

  private BigDecimal custoDaCompra(Operacao operacao) {
    return operacao
        .getValorBruto()
        .add(operacao.getCorretagem())
        .add(operacao.getImpostos())
        .add(operacao.getValorAdicional());
  }

  private BigDecimal receitaLiquidaVenda(Operacao operacao) {
    return operacao
        .getValorBruto()
        .subtract(operacao.getCorretagem())
        .subtract(operacao.getImpostos())
        .subtract(operacao.getValorAdicional());
  }

  private BigDecimal dividir(BigDecimal dividendo, BigDecimal divisor) {
    return divisor.signum() == 0 ? ZERO : dividendo.divide(divisor, 8, RoundingMode.HALF_UP);
  }

  public record Resultado(
      BigDecimal valorAtual,
      BigDecimal precoMedio,
      BigDecimal precoMedioVenda,
      BigDecimal lucroPrejuizoRealizado,
      BigDecimal lucroPrejuizoNaoRealizado,
      BigDecimal lucroPrejuizo,
      BigDecimal valorInvestidoAtual,
      BigDecimal valorTotalComprado,
      BigDecimal valorTotalVendido) {}
}
