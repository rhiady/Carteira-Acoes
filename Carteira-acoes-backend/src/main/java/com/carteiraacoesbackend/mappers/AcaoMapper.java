package com.carteiraacoesbackend.mappers;

import com.carteiraacoesbackend.domains.Acao;
import com.carteiraacoesbackend.domains.enums.Mercado;
import com.carteiraacoesbackend.dto.AcaoResponse;
import com.carteiraacoesbackend.services.TaxaUsdBrl;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AcaoMapper {

  public AcaoResponse toResponse(Acao acao) {
    return toResponse(acao, Optional.empty());
  }

  public AcaoResponse toResponse(Acao acao, Optional<TaxaUsdBrl> taxa) {
    boolean converter = acao.getMercado() == Mercado.EUA && taxa.isPresent();
    BigDecimal cotacaoEmBrl =
        converter ? acao.getCotacaoAtual().multiply(taxa.get().valor()) : null;
    return new AcaoResponse(
        acao.getId(),
        acao.getTicker(),
        acao.getNomeEmpresa(),
        acao.getMercado(),
        acao.getMoeda(),
        acao.getCotacaoAtual(),
        acao.getDataHoraCotacao(),
        cotacaoEmBrl,
        converter ? taxa.get().valor() : null,
        converter ? taxa.get().dataHoraReferencia() : null,
        acao.getCreatedAt(),
        acao.getUpdatedAt());
  }
}
